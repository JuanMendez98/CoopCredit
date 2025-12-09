package com.crudzao.creditapp.infrastructure.config;

import com.crudzao.creditapp.infrastructure.exception.BusinessException;
import com.crudzao.creditapp.infrastructure.exception.InvalidStateException;
import com.crudzao.creditapp.infrastructure.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST API.
 * Returns ProblemDetail (RFC 7807) format for all errors with traceId.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }

    private String getRequestPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    private void enrichProblemDetail(ProblemDetail problemDetail, WebRequest request, String traceId) {
        problemDetail.setInstance(URI.create(getRequestPath(request)));
        problemDetail.setProperty("timestamp", LocalDateTime.now().toString());
        problemDetail.setProperty("traceId", traceId);
    }

    /**
     * Handle resource not found exceptions.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        String traceId = generateTraceId();
        log.warn("[{}] Resource not found: {}", traceId, ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND.value());
        problemDetail.setType(URI.create("https://api.example.com/errors/not-found"));
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setDetail(ex.getMessage());
        enrichProblemDetail(problemDetail, request, traceId);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    /**
     * Handle business rule violations.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusinessException(
            BusinessException ex, WebRequest request) {
        String traceId = generateTraceId();
        log.warn("[{}] Business rule violated: {}", traceId, ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST.value());
        problemDetail.setType(URI.create("https://api.example.com/errors/business-rule"));
        problemDetail.setTitle("Business Rule Violation");
        problemDetail.setDetail(ex.getMessage());
        enrichProblemDetail(problemDetail, request, traceId);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    /**
     * Handle invalid state exceptions.
     */
    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<ProblemDetail> handleInvalidStateException(
            InvalidStateException ex, WebRequest request) {
        String traceId = generateTraceId();
        log.warn("[{}] Invalid state: {}", traceId, ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT.value());
        problemDetail.setType(URI.create("https://api.example.com/errors/invalid-state"));
        problemDetail.setTitle("Invalid State");
        problemDetail.setDetail(ex.getMessage());
        enrichProblemDetail(problemDetail, request, traceId);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    /**
     * Handle validation errors from @Valid annotation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        String traceId = generateTraceId();

        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("[{}] Validation error: {}", traceId, errors);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST.value());
        problemDetail.setType(URI.create("https://api.example.com/errors/validation"));
        problemDetail.setTitle("Validation Failed");
        problemDetail.setDetail("Validation errors: " + errors);
        enrichProblemDetail(problemDetail, request, traceId);
        problemDetail.setProperty("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    /**
     * Handle access denied exceptions.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        String traceId = generateTraceId();
        log.warn("[{}] Access denied: {}", traceId, ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN.value());
        problemDetail.setType(URI.create("https://api.example.com/errors/access-denied"));
        problemDetail.setTitle("Access Denied");
        problemDetail.setDetail("You do not have permission to access this resource");
        enrichProblemDetail(problemDetail, request, traceId);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetail);
    }

    /**
     * Handle generic exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
            Exception ex, WebRequest request) {
        String traceId = generateTraceId();
        log.error("[{}] Unexpected error", traceId, ex);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        problemDetail.setType(URI.create("https://api.example.com/errors/internal-server-error"));
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setDetail("An unexpected error occurred. Reference: " + traceId);
        enrichProblemDetail(problemDetail, request, traceId);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}
