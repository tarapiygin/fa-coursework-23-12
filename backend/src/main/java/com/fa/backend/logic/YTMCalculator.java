package com.fa.backend.logic;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.solvers.NewtonRaphsonSolver;
import org.apache.commons.math3.exception.DimensionMismatchException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Класс для расчета доходности к погашению (YTM) облигаций.
 * <p>
 * Этот класс предоставляет методы для расчета приблизительной YTM облигаций,
 * используя базовую формулу. Расчеты основаны на текущей рыночной цене облигации,
 * ее номинальной стоимости, годовой купонной ставке и количестве лет до погашения.
 * </p>
 */
public class YTMCalculator {

    public static double getPrice(Double coupon, Double faceValue, Double intRate, int years, int freq) {
        double totalCouponsPV = getCouponsPV(years, freq, coupon, intRate);
        double faceValuePV = getFaceValuePV(faceValue, intRate, years);
        return totalCouponsPV + faceValuePV;
    }

    public static double getFaceValuePV(double faceValue, double intRate, int years) {
        return faceValue / Math.pow((1 + intRate), years);
    }

    public static double getCouponsPV(int years, int freq, double coupon, Double intRate) {
        double pv = 0;
        for (int period = 0; period < years * freq; period++) {
            pv += coupon / Math.pow((1 + intRate / freq), period + 1);
        }
        return pv;
    }

    private static double getPriceDerivative(double coupon, double faceValue, double ytm, int years, int freq) {
        double derivative = 0;
        for (int period = 1; period <= years * freq; period++) {
            derivative -= (period * coupon) / Math.pow(1 + ytm / freq, period + 1);
        }
        derivative -= (years * freq * faceValue) / Math.pow(1 + ytm / freq, years * freq + 1);
        return derivative;
    }

    public static double getYTM(Double coupon, Double faceValue, int years, int freq, Double bondPrice) {
        // Use the NewtonRaphsonSolver to find the root of the function, i.e., where it equals zero
        NewtonRaphsonSolver solver = new NewtonRaphsonSolver(1E-5); // Precision of 1E-6

        UnivariateDifferentiableFunction function = new UnivariateDifferentiableFunction() {
            @Override
            public double value(double x) {
                return getPrice(coupon, faceValue, x, years, freq) - bondPrice;
            }
            @Override
            public DerivativeStructure value(DerivativeStructure t) throws DimensionMismatchException {
                double value = value(t.getValue());
                double derivative = getPriceDerivative(coupon, faceValue, t.getValue(), years, freq);
                return new DerivativeStructure(t.getFreeParameters(), t.getOrder(), value, derivative);
            }
        };

        double ytm = solver.solve(10000, function, coupon / faceValue); // Here, we assume YTM is between 0 and 1 (0% to 100%)
        return ytm;
    }

    /**
     * Вычисляет доходность к погашению (YTM) для облигации с использованием метода Ньютона-Рафсона.
     * <p>
     * Применяем численный метод Ньютона-Рафсона для нахождения YTM, что позволяет
     * получить более точное значение по сравнению с упрощенными методами расчета. Метод
     * Ньютона-Рафсона итеративно приближается к точному значению YTM, используя производную
     * функции текущей стоимости облигации. Этот процесс требует начального предположения и
     * продолжается до достижения заданной точности.
     * </p>
     * <p>
     * Результат зависит от выбранного начального приближения и может
     * потребовать нескольких итераций для достижения желаемой точности.
     * Метод может не сходиться для некоторых входных данных:
     * 1. Крайне низкие или высокие значения купонной ставки
     * 2. Короткий срок до погашения с высокой рыночной ценой
     * 3. Особенности функции текущей стоимости
     * </p>
     * <p>
     * Рыночная цена облигации: 1200
     * Номинальная стоимость: 1000
     * Купонная ставка: 0.01 (1%)
     * Количество лет до погашения: 1
     * </p>
     *
     * @param marketPrice     Текущая рыночная цена облигации.
     * @param faceValue       Номинальная (лицевая) стоимость облигации.
     * @param couponRate      Годовая купонная ставка облигации. Это процент от номинальной
     *                        стоимости, который выплачивается владельцу облигации ежегодно.
     * @param maturityDate    Дата погашения облигации
     * @param tolerance       Порог точности расчетов, выражается как десятичная дробь.
     * @param paymentsPerYear Количество выплат купонов в год
     * @return Рассчитанное значение YTM для облигации. YTM выражается как десятичная дробь.
     * @throws IllegalArgumentException если один из входных параметров недопустим.
     *                                  Например, если marketPrice, faceValue или couponRate
     *                                  отрицательны, или если yearsToMaturity не является положительным.
     */
    public static double calculateNewtonRaphson(double marketPrice, double faceValue, double couponRate, LocalDate maturityDate, double tolerance, int paymentsPerYear) {
        // Проверка входных параметров на допустимость
        if (marketPrice < 0 || faceValue < 0 || couponRate < 0 || paymentsPerYear <= 0) {
            throw new IllegalArgumentException("Недопустимые значения входных параметров");
        }

        LocalDate today = LocalDate.now();
        double d = ChronoUnit.MONTHS.between(today, maturityDate) / 12.0 * paymentsPerYear;
        // Количество лет выплаты купонов по облигации до момента ее погашения
        double n = (Math.round(d * 2) / 2.0);
        int totalPeriods = (int) Math.round(n);

        double coupon = faceValue * couponRate; // Годовая купонная выплата
        double totalPayments = totalPeriods * paymentsPerYear; // Общее количество платежей
        double periodicCoupon = coupon / paymentsPerYear; // Купонный платеж за период
        double ytm = getYTM(periodicCoupon, faceValue, totalPeriods, paymentsPerYear, marketPrice); // Начальное приближение YTM
//        double ytmPrev; // Переменная для хранения предыдущего значения YTM
//        double diff; // Переменная для хранения производной
//        do {
//            // Сохраняем текущее значение YTM для последующего сравнения
//            ytmPrev = ytm;
//            // Инициализируем переменные для функции (f) и ее производной (df)
//            double f = 0;
//            double df = 0;
//            diff = 0;
//            // Рассчитываем текущую стоимость купонных платежей и их производных
//            for (int i = 1; i <= totalPayments; i++) {
//                // Текущая стоимость i-го купонного платежа
//                f += periodicCoupon / Math.pow(1 + ytm, i);
//
//                // Производная текущей стоимости i-го купонного платежа
//                df -= i * periodicCoupon / Math.pow(1 + ytm, i + 1) ;
//            }
////            // Добавляем текущую стоимость погашения номинала и ее производную
//            f += faceValue / Math.pow(1 + ytm, totalPayments + 1) - marketPrice;
//            df -= (totalPayments + 1) * faceValue  / Math.pow(1 + ytm, totalPayments + 2);
//            // Обновляем YTM с использованием метода Ньютона-Рафсона
//            ytm = ytm - f / df;
//            // Вычисляем абсолютную разницу между текущим и предыдущим значением YTM
//            diff = Math.abs(ytm - ytmPrev);
//            System.out.println(ytm);
//        } while (diff > 0.00001); // Повторяем, пока разница не станет меньше заданного порога
        return ytm * 100; // Преобразуем YTM в годовое значение
    }

    /**
     * Вычисляет приблизительную доходность к погашению (YTM) для облигации.
     * <p>
     * Этот метод использует упрощенную формулу для расчета YTM, которая предполагает,
     * что все купонные выплаты реинвестируются на ту же доходность. Результат может
     * не отражать точную доходность облигации.
     * </p>
     *
     * @param marketPrice     Текущая рыночная цена облигации.
     * @param faceValue        Номинальная стоимость облигации.
     * @param couponRate      Годовая купонная ставка облигации (в десятичной форме).
     * @param yearsToMaturity Количество лет до погашения облигации.
     * @param paymentsPerYear Количество выплат купонов в год
     * @return Приблизительное значение YTM в десятичной форме.
     */
    public static double calculateApproximateYTM(double marketPrice, double faceValue, double couponRate, double yearsToMaturity, int paymentsPerYear) {
        double totalPayments = Math.ceil(yearsToMaturity * paymentsPerYear); // Общее количество платежей
        double periodicCoupon = (faceValue * couponRate) / paymentsPerYear; // Купонный платеж за период
        double averagePrice = (faceValue + marketPrice) / 2;

        // Рассчитываем приблизительную YTM для каждого периода
        double ytmPerPeriod = (periodicCoupon + (faceValue - marketPrice) / totalPayments) / averagePrice;

        // Преобразуем периодическую YTM в годовую
        return ytmPerPeriod * paymentsPerYear;
    }

}
