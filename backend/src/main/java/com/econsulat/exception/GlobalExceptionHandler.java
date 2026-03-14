package com.econsulat.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> fields = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalide",
                        (a, b) -> a));

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", messageSource.getMessage("error.validation", null, getLocale()));
        errorResponse.put("message", "Erreurs de validation");
        errorResponse.put("fields", fields);
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("path", request.getDescription(false));
        errorResponse.put("type", "VALIDATION_ERROR");

        log.debug("Validation échouée - path: {}, champs: {}", request.getDescription(false), fields.keySet());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", messageSource.getMessage("error.validation", null, getLocale()));
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("path", request.getDescription(false));
        errorResponse.put("type", "VALIDATION_ERROR");

        log.error("Erreur runtime - path: {} - message: {}", request.getDescription(false), ex.getMessage(), ex);

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

        log.warn("Données invalides - path: {} - message: {}", request.getDescription(false), ex.getMessage());

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

        log.error("Erreur interne - path: {}", request.getDescription(false), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
