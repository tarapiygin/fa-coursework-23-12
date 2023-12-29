package com.fa.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Класс для передачи данных о сделке
 */
@Data
public class PurchaseItemCreateDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date creationDateTime;
    private Integer quantity;
    private Double price;
    private String bondTicket; // Только ticket облигации
    public PurchaseItemCreateDTO(){}
}
