package com.fa.dto;

import lombok.Data;

/**
 * Базовый класс для получения данных о доходности каждой облигации
 */
@Data
public class YTMItemDTO {
    private String ticket;
    private double ytm;
    private double weightInPortfolio;
}
