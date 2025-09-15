package com.tm.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void constructor_WithParameters_Success() {
        LoginRequest request = new LoginRequest("testuser", "password123");
        
        assertEquals("testuser", request.getUsername());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void defaultConstructor_Success() {
        LoginRequest request = new LoginRequest();
        
        assertNull(request.getUsername());
        assertNull(request.getPassword());
    }

    @Test
    void settersAndGetters_Success() {
        LoginRequest request = new LoginRequest();
        
        request.setUsername("newuser");
        request.setPassword("newpass");
        
        assertEquals("newuser", request.getUsername());
        assertEquals("newpass", request.getPassword());
    }
}