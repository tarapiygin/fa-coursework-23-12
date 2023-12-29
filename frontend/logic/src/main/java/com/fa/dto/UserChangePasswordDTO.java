package com.fa.dto;

import lombok.Data;


/**
 * Класс для передачи данных об изменении пароля пользователя
 */
@Data
public class UserChangePasswordDTO {
    private String username;
    private String password;
    private String newPassword;
}
