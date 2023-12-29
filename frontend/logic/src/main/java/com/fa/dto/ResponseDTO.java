package com.fa.dto;

/**
 * Базовый класс обработки данных поступающих с сервера. Необходим для обработки ошибок и унификации методов API
 */
public class ResponseDTO {
    public final boolean isOk;
    public final String error;

    public final String data;

    public ResponseDTO(boolean isOk, String error, String data) {
        this.isOk = isOk;
        this.error = error;
        this.data = data;
    }
}
