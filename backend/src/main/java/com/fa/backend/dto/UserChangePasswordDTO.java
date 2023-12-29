package com.fa.backend.dto;

import lombok.Data;

/**
 * Класс Data Transfer Object для изменения пароля пользователя с учетом текущего пароля
 */
@Data
public class UserChangePasswordDTO {
    private String username;
    private String newPassword;
    private String password;
}
