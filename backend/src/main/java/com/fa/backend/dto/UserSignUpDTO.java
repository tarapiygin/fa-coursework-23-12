package com.fa.backend.dto;

import lombok.Data;

/**
 * Класс Data Transfer Object для регистрации пользователя
 */
@Data
public class UserSignUpDTO {
    private String username;
    private String password;
}
