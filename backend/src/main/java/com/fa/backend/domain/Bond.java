package com.fa.backend.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

/**
 * Класс модели для Облигации
 */
@Entity
@Data
public class Bond {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String ticket;
    private Double marketPrice;
    private String name;
    // Номинальная цена облигации при погашении
    private Double parValue;
    // Годовая купонная Ставка %
    private Double couponRate;
    private int paymentsPerYear;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    // Дата погашения облигации
    private Date maturityDate;


    public Bond() {
    }

    public Bond(String ticket, Double marketPrice, String name, Double parValue, Double couponRate, int paymentsPerYear, Date maturityDate) {
        this.ticket = ticket;
        this.marketPrice = marketPrice;
        this.name = name;
        this.parValue = parValue;
        this.couponRate = couponRate;
        this.paymentsPerYear = paymentsPerYear;
        this.maturityDate = maturityDate;
    }
}
