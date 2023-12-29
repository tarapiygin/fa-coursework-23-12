package com.fa.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Класс Data Transfer Object для создания сделки
 */
@Data
public class PurchaseCreateDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date creationDateTime;
    private Integer quantity;
    private Double price;
    private String bondTicket; // Только ticket облигации
}
