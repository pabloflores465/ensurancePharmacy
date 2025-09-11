package com.sources.app.exceptions;

/**
 * Dedicated exception to represent failures when communicating with external
 * services (hospital, pharmacy, etc.). Prefer throwing this instead of generic
 * Exception to improve diagnostics and handling.
 */
public class ExternalServiceException extends Exception {
    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
