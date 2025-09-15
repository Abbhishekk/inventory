package com.tm.auth.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthExceptionTest {

    @Test
    void constructor_WithMessage_Success() {
        String message = "Authentication failed";
        AuthException exception = new AuthException(message);
        
        assertEquals(message, exception.getMessage());
    }

    @Test
    void constructor_WithMessageAndCause_Success() {
        String message = "Authentication failed";
        Throwable cause = new RuntimeException("Root cause");
        AuthException exception = new AuthException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}