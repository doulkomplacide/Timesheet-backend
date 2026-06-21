package com.gestiontemps.exception;

import com.gestiontemps.dto.reponse.ReponseApi;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RessourceNonTrouveeException.class)
    public ResponseEntity<ReponseApi<?>> handleResourceNotFound(RessourceNonTrouveeException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ReponseApi.error(ex.getMessage()));
    }

    @ExceptionHandler(RequeteInvalideException.class)
    public ResponseEntity<ReponseApi<?>> handleBadRequest(RequeteInvalideException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ReponseApi.error(ex.getMessage()));
    }

    @ExceptionHandler(NonAutoriseException.class)
    public ResponseEntity<ReponseApi<?>> handleUnauthorized(NonAutoriseException ex) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ReponseApi.error(ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ReponseApi<?>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ReponseApi.error("Email ou mot de passe incorrect"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ReponseApi<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ReponseApi.error("Erreur de validation", errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ReponseApi<?>> handleConstraintViolation(ConstraintViolationException ex) {
        String errors = ex.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ReponseApi.error("Erreur de validation: " + errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ReponseApi<?>> handleGenericException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ReponseApi.error("Une erreur interne est survenue"));
    }
}