package com.fa.dto;

import lombok.Data;


/**
 * Класс для получения данных о пользователе
 */
@Data
public class UserGetDTO {
    private Long id;
    private String username;
}
