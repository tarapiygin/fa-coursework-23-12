package com.fa.dto;

import lombok.Data;


/**
 * Базовый класс данных об объекте портфеля облигаций
 */
@Data
public class UserPortfolioItemDTO {
    public String ticket;
    public String name;

    public int quantity;
    public double averageCost;

    public Double parValue;
    public Double couponRate;

    public String maturityDate;
    public int paymentsPerYear;


    public UserPortfolioItemDTO(){}
}
