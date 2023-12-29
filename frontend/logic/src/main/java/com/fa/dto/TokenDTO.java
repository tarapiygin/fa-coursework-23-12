package com.fa.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Класс для получения данных о токене
 */
@Data
public class TokenDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    public Date issuedAt;
    public Long lifetime;
    public String token;

    public TokenDTO(){

    }
}
