package com.fa.dto;

import lombok.Data;

/**
 * Класс для получения данных о сделке
 */
@Data
public class PurchaseItemDTO {
    public Long id;

    public String creationDateTime;
    public int quantity;
    public double price;
    public String bondTicket;

    public PurchaseItemDTO(){}
}
