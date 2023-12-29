package com.fa.backend.dto;

import lombok.Data;

/**
 * Класс Data Transfer Object для авторизации пользователя
 */
@Data
public class UserSignInDTO {
    private String username;
    private String password;
}
