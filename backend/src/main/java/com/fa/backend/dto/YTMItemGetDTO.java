package com.fa.backend.dto;

import lombok.Data;

/**
 * Класс Data Transfer Object для представления рассчитанной доходности портфеля пользователя
 */
@Data
public class YTMItemGetDTO {
    private String ticket;
    private double ytm;
    private double weightInPortfolio;
}
