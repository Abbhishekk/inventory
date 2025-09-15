package com.tm.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    @Test
    void constructor_WithAllParameters_Success() {
        AuthResponse response = new AuthResponse("access-token", "refresh-token", "testuser");
        
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("testuser", response.getUsername());
    }

    @Test
    void settersAndGetters_Success() {
        AuthResponse response = new AuthResponse();
        
        response.setAccessToken("new-access-token");
        response.setRefreshToken("new-refresh-token");
        response.setUsername("newuser");
        
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        assertEquals("newuser", response.getUsername());
    }
}