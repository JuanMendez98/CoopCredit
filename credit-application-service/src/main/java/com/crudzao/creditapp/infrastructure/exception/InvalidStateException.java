package com.crudzao.creditapp.infrastructure.exception;

/**
 * Exception thrown when an entity is in an invalid state for the requested operation.
 */
public class InvalidStateException extends RuntimeException {
    public InvalidStateException(String message) {
        super(message);
    }

    public InvalidStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
