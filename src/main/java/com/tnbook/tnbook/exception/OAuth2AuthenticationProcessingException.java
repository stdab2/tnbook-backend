package com.tnbook.tnbook.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
@Setter
public class OAuth2AuthenticationProcessingException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3L;

    final HttpStatus errorCode;

    public OAuth2AuthenticationProcessingException(HttpStatus errorCode, String errorMessage, Throwable errorCause) {
        super(errorMessage, errorCause);
        this.errorCode = errorCode;
    }

    public OAuth2AuthenticationProcessingException(HttpStatus errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }
}
