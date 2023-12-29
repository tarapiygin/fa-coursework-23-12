package com.fa.backend.logic;

import com.fa.backend.domain.Bond;
import com.fa.backend.domain.Purchase;
import com.fa.backend.dto.PortfolioItemDTO;
import com.fa.backend.dto.YTMItemGetDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Вспомогательный класс
 */
public class Utils {

    public static double calculateYearsToMaturity(Date maturityDate) {
        LocalDate maturityLocalDate = maturityDate.toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
        LocalDate today = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(today, maturityLocalDate);
        return daysBetween / 365.25; // Делим на 365.25, учитывая високосные годы
    }

    public static double calculateTotalPortfolioYTM(List<YTMItemGetDTO> ytmItems, double tolerance) {
        double weightedYTM = ytmItems.stream()
                .mapToDouble(item -> item.getYtm() * item.getWeightInPortfolio())
                .sum();
        return roundDouble(weightedYTM, tolerance);
    }

    public static List<PortfolioItemDTO> calculatePortfolio(List<Purchase> purchases) {
        return purchases.stream()
                .collect(
                        Collectors.groupingBy(
                                purchase -> purchase
                                        .getBond()
                                        .getTicket()
                        )
                ).entrySet()
                .stream()
                .map(entry -> {
                    String ticket = entry.getKey();
                    List<Purchase> bondPurchases = entry.getValue();

                    int totalQuantity = bondPurchases.stream().mapToInt(Purchase::getQuantity).sum();
                    double totalCost = bondPurchases.stream().mapToDouble(purchase -> purchase.getPrice() * purchase.getQuantity()).sum();
                    double averageCost = totalCost / totalQuantity;

                    Bond bond = bondPurchases.get(0).getBond(); // Предполагаем, что все покупки относятся к одной и той же облигации

                    PortfolioItemDTO item = new PortfolioItemDTO();
                    item.setTicket(ticket);

                    item.setQuantity(totalQuantity);
                    item.setAverageCost(averageCost);

                    item.setName(bond.getName());
                    item.setParValue(bond.getParValue());
                    item.setCouponRate(bond.getCouponRate());
                    item.setMaturityDate(bond.getMaturityDate());
                    item.setPaymentsPerYear(bond.getPaymentsPerYear());

                    return item;
                }).collect(Collectors.toList());
    }

    public static List<YTMItemGetDTO> calculatePortfolioYTM(List<Purchase> purchases, double tolerance, String method) {
        double totalPortfolioValue = purchases.stream()
                .mapToDouble(purchase -> purchase.getBond().getMarketPrice() * purchase.getQuantity())
                .sum();

        Map<String, List<Purchase>> groupedByTicket = purchases.stream()
                .collect(Collectors.groupingBy(purchase -> purchase.getBond().getTicket()));

        List<YTMItemGetDTO> ytmItems = new ArrayList<>();
        for (Map.Entry<String, List<Purchase>> entry : groupedByTicket.entrySet()) {
            String ticket = entry.getKey();
            List<Purchase> bondPurchases = entry.getValue();

            Bond bond = bondPurchases.get(0).getBond(); // Получаем данные облигации

            int totalQuantity = bondPurchases.stream().mapToInt(Purchase::getQuantity).sum(); // общее количество облигаций
            double totalCost = bondPurchases.stream()
                    .mapToDouble(purchase -> purchase.getPrice() * purchase.getQuantity()).sum(); // сумма потраченная на покупку

            double averageCost = totalCost / totalQuantity; // средняя цена 1 купленной облигации

            double parValue = bond.getParValue();
            double couponRate = bond.getCouponRate();
            double yearsToMaturity = calculateYearsToMaturity(bond.getMaturityDate()); // Количество лет до погашения

            double ytm;

            if (Objects.equals(method, "NEWTON")) {
                ytm = YTMCalculator.calculateNewtonRaphson(
                        averageCost,
                        parValue,
                        couponRate,
                        yearsToMaturity,
                        tolerance,
                        bond.getPaymentsPerYear()
                );
            } else {
                ytm = YTMCalculator.calculateApproximateYTM(
                        averageCost,
                        parValue,
                        couponRate,
                        yearsToMaturity,
                        bond.getPaymentsPerYear()
                );
            }

            double totalBondValue = bondPurchases.stream()
                    .mapToDouble(purchase -> purchase.getBond().getMarketPrice() * purchase.getQuantity())
                    .sum();

            double weightInPortfolio = totalBondValue / totalPortfolioValue;

            YTMItemGetDTO item = new YTMItemGetDTO();
            item.setTicket(ticket);
            item.setYtm(roundDouble(ytm, tolerance));
            item.setWeightInPortfolio(roundDouble(weightInPortfolio, tolerance));
            ytmItems.add(item);
        }

        return ytmItems;
    }

    public static double roundDouble(double d, double tolerance) {
        int decimalPlaces = (int) Math.abs(Math.log10(tolerance));
        BigDecimal bigDecimal = new BigDecimal(d);
        bigDecimal = bigDecimal.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
}
