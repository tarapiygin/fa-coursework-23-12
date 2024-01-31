package com.fa.backend.logic;

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
        long totalPeriods = ChronoUnit.YEARS.between(today, maturityDate); // Количество лет выплаты купонов по облигации до момента ее погашения

        double coupon = faceValue * couponRate; // Годовая купонная выплата
        double totalPayments = totalPeriods * paymentsPerYear; // Общее количество платежей
        double periodicCoupon = coupon / paymentsPerYear; // Купонный платеж за период
        double ytm = couponRate; // Начальное приближение YTM
        double ytmPrev; // Переменная для хранения предыдущего значения YTM
        double diff; // Переменная для хранения производной
        do {
            // Сохраняем текущее значение YTM для последующего сравнения
            ytmPrev = ytm;
            // Инициализируем переменные для функции (f) и ее производной (df)
            double f = 0;
            double df = 0;
            // Рассчитываем текущую стоимость купонных платежей и их производных
            for (int i = 1; i <= totalPayments; i++) {
                // Текущая стоимость i-го купонного платежа
                f += periodicCoupon / Math.pow(1 + ytm, i)  ;

                // Производная текущей стоимости i-го купонного платежа
                df -= i * periodicCoupon / Math.pow(1 + ytm, 1 + i);
            }
            // Добавляем текущую стоимость погашения номинала и ее производную
            f += faceValue / Math.pow(1 + ytm, totalPayments) - marketPrice;
            df -= faceValue * totalPayments * Math.pow(1 + ytm, -1 - totalPayments);
            // Обновляем YTM с использованием метода Ньютона-Рафсона
            ytm = ytm - f / df;
            // Вычисляем абсолютную разницу между текущим и предыдущим значением YTM
            diff = Math.abs(ytm - ytmPrev);
        } while (diff > tolerance); // Повторяем, пока разница не станет меньше заданного порога
        return ytm; // Преобразуем YTM в годовое значение
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
     * @param parValue        Номинальная стоимость облигации.
     * @param couponRate      Годовая купонная ставка облигации (в десятичной форме).
     * @param yearsToMaturity Количество лет до погашения облигации.
     * @param paymentsPerYear Количество выплат купонов в год
     * @return Приблизительное значение YTM в десятичной форме.
     */
    public static double calculateApproximateYTM(double marketPrice, double parValue, double couponRate, double yearsToMaturity, int paymentsPerYear) {
        double totalPayments = Math.ceil(yearsToMaturity * paymentsPerYear); // Общее количество платежей
        double periodicCoupon = (parValue * couponRate) / paymentsPerYear; // Купонный платеж за период
        double averagePrice = (parValue + marketPrice) / 2;

        // Рассчитываем приблизительную YTM для каждого периода
        double ytmPerPeriod = (periodicCoupon + (parValue - marketPrice) / totalPayments) / averagePrice;

        // Преобразуем периодическую YTM в годовую
        return ytmPerPeriod * paymentsPerYear;
    }
}
