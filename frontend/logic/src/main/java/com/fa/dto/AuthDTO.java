package com.fa.dto;

import lombok.Data;

/**
 * Класс для передачи данных авторизации
 */
@Data
public class AuthDTO {
    public final String username;
    public final String password;

    public AuthDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}