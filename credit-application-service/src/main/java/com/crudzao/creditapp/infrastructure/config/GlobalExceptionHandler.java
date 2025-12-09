package com.crudzao.creditapp.infrastructure.config;

import com.crudzao.creditapp.infrastructure.exception.BusinessException;
import com.crudzao.creditapp.infrastructure.exception.InvalidStateException;
import com.crudzao.creditapp.infrastructure.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST API.
 * Returns ProblemDetail (RFC 7807) format for all errors.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle resource not found exceptions.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND.value());
        problemDetail.setType(URI.create("https://api.example.com/errors/not-found"));
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        problemDetail.setProperty("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    /**
     * Handle business rule violations.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusinessException(
            BusinessException ex, WebRequest request) {
        log.warn("Business rule violated: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST.value());
        problemDetail.setType(URI.create("https://api.example.com/errors/business-rule"));
        problemDetail.setTitle("Business Rule Violation");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        problemDetail.setProperty("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    /**
     * Handle invalid state exceptions.
     */
    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<ProblemDetail> handleInvalidStateException(
            InvalidStateException ex, WebRequest request) {
        log.warn("Invalid state: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT.value());
        problemDetail.setType(URI.create("https://api.example.com/errors/invalid-state"));
        problemDetail.setTitle("Invalid State");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        problemDetail.setProperty("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    /**
     * Handle validation errors from @Valid annotation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("Validation error: {}", ex.getMessage());

        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST.value());
        problemDetail.setType(URI.create("https://api.example.com/errors/validation"));
        problemDetail.setTitle("Validation Failed");
        problemDetail.setDetail("Validation errors: " + errors);
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    /**
     * Handle generic exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
            Exception ex, WebRequest request) {
        log.error("Unexpected error", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        problemDetail.setType(URI.create("https://api.example.com/errors/internal-server-error"));
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setDetail("An unexpected error occurred");
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        problemDetail.setProperty("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}
