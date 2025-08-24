package com.tnbook.tnbook.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ErrorDetails> handleEmailSendingException(EmailSendingException e) {
        ErrorDetails errorDetails = new ErrorDetails(e.getErrorCode().value(), e.getMessage());
        return new ResponseEntity<>(errorDetails, e.getErrorCode());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDetails> handleAuthenticationException(AuthenticationException e) {
        ErrorDetails errorDetails = new ErrorDetails(e.getErrorCode().value(), e.getMessage());
        return new ResponseEntity<>(errorDetails, e.getErrorCode());
    }

    @ExceptionHandler(OAuth2AuthenticationProcessingException.class)
    public ResponseEntity<ErrorDetails> handleOAuth2AuthenticationProcessingException(OAuth2AuthenticationProcessingException e) {
        ErrorDetails errorDetails = new ErrorDetails(e.getErrorCode().value(), e.getMessage());
        return new ResponseEntity<>(errorDetails, e.getErrorCode());
    }
}
