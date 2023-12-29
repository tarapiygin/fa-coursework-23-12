package com.fa.dto;

import lombok.Data;

import java.util.List;

/**
 * Класс обертка для получения данных о текущей доходности портфеля к погашению
 */
@Data
public class YTMListDTO {
    private List<YTMItemDTO> ytmList;
    private double ytmPortfolio;
}
