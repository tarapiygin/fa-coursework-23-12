package com.fa.backend.dto;

import lombok.Data;

import java.util.List;

/**
 * Класс Data Transfer Object для сериализации доходности Облигации
 */
@Data
public class YTMListDTO {
    private List<YTMItemGetDTO> ytmList;
    private double ytmPortfolio;
}
