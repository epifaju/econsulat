package com.econsulat.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private Locale getLocale() {
        Locale requestLocale = LocaleContextHolder.getLocale();
        if ("pt".equals(requestLocale.getLanguage())) {
            return new Locale("pt");
        }
        return Locale.FRENCH;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", messageSource.getMessage("error.validation", null, getLocale()));
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("path", request.getDescription(false));
        errorResponse.put("type", "VALIDATION_ERROR");

        // Log de l'erreur pour debugging
        System.err.println("❌ GlobalExceptionHandler - Erreur Runtime: " + ex.getMessage());
        System.err.println("❌ GlobalExceptionHandler - Path: " + request.getDescription(false));
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex,
            WebRequest request) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", messageSource.getMessage("error.invalidData", null, getLocale()));
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("path", request.getDescription(false));
        errorResponse.put("type", "INVALID_DATA");

        // Log de l'erreur pour debugging
        System.err.println("❌ GlobalExceptionHandler - Données invalides: " + ex.getMessage());
        System.err.println("❌ GlobalExceptionHandler - Path: " + request.getDescription(false));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, WebRequest request) {
        Locale locale = getLocale();
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", messageSource.getMessage("error.internal", null, locale));
        errorResponse.put("message", messageSource.getMessage("error.unexpected", null, locale));
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("path", request.getDescription(false));

        // Log de l'erreur pour debugging
        System.err.println("❌ Erreur Générique: " + ex.getMessage());
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
