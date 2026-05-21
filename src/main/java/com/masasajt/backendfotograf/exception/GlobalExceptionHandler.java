package com.masasajt.backendfotograf.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> errors = new HashMap<>();
        // Pakujemo tvoju poruku iz servisa u ključ "message" koji frontend očekuje
        errors.put("message", ex.getMessage());

        // Vraćamo HTTP status 400 Bad Request umesto teške greške 500
        return ResponseEntity.badRequest().body(errors);
    }
}