package com.fa.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;


/**
 * Класс Data Transfer Object для передачи данных о Портфолио
 */
@Data
public class PortfolioItemDTO {
    private String ticket;
    private String name;

    private int quantity;
    private double averageCost;

//    private double marketPrice;
//    private double profit;
//    private double profitPercent;

    private Double parValue;
    private Double couponRate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date maturityDate;
    private int paymentsPerYear;
}
