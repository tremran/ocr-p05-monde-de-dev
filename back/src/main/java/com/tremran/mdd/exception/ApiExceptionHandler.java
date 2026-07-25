package com.tremran.mdd.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduit les exceptions métier en réponses HTTP homogènes.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Convertit une absence de ressource métier en réponse HTTP 404.
     *
     * @param exception exception métier capturée
     * @return réponse standardisée de type not found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException exception) {
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    /**
     * Convertit un conflit métier en réponse HTTP 409.
     *
     * @param exception exception métier capturée
     * @return réponse standardisée de type conflict
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleConflict(ConflictException exception) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    /**
     * Masque les erreurs d'authentification derrière une réponse HTTP 401 uniforme.
     *
     * @param exception exception Spring Security capturée
     * @return réponse standardisée de type unauthorized
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException exception) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    /**
     * Retourne le premier message de validation utile sous la forme d'une réponse HTTP 400.
     *
     * @param exception exception de validation Spring MVC capturée
     * @return réponse standardisée de type bad request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("Validation failed");
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseEntity<Map<String, String>> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "error", status.getReasonPhrase(),
                "message", message));
    }
}