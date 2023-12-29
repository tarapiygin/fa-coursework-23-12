package com.fa.api;

import java.io.IOException;

/**
 * Класс обертка для обработки ошибок возникших при обращении к API
 */
public class RequestError extends IOException {

    // Конструктор без параметров
    public RequestError() {
        super();
    }

    // Конструктор с сообщением об ошибке
    public RequestError(String message) {
        super(message);
    }

    // Конструктор с сообщением об ошибке и причиной (cause)
    public RequestError(String message, Throwable cause) {
        super(message, cause);
    }

    // Конструктор с причиной (cause)
    public RequestError(Throwable cause) {
        super(cause);
    }
}
