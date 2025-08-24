package com.tnbook.tnbook.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
@Setter
public class EmailSendingException extends Exception{

    @Serial
    private static final long serialVersionUID = 1L;

    final HttpStatus errorCode;

    public EmailSendingException(HttpStatus errorCode, String errorMessage, Throwable errorCause) {
        super(errorMessage, errorCause);
        this.errorCode = errorCode;
    }
}
