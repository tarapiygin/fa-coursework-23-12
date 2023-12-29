package com.fa.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Класс Data Transfer Object для представления токена
 */
@Data
public class TokenGetDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date issuedAt;
    private Long lifetime;
    private String token;

    public TokenGetDTO() {

    }
}
