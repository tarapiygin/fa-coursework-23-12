package com.fa.backend.dto;

import lombok.Data;

/**
 * Класс Data Transfer Object представления пользователя
 */
@Data
public class UserGetDTO {
    private Long id;
    private String username;
}
