package com.fa.dto;

import lombok.Data;

/**
 * Класс для получения и передачи данных об облигации
 */
@Data
public class BondItemDTO {
    public Long id;
    public String ticket;
    public Double marketPrice;
    public String name;
    public Double parValue;
    public Double couponRate;
    public String maturityDate;
    public int paymentsPerYear;

    public BondItemDTO() {
    }
}
