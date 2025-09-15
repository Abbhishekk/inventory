package com.tm.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenRequestTest {

    @Test
    void constructor_WithParameter_Success() {
        RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");
        
        assertEquals("refresh-token", request.getRefreshToken());
    }

    @Test
    void defaultConstructor_Success() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        
        assertNull(request.getRefreshToken());
    }

    @Test
    void setterAndGetter_Success() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        
        request.setRefreshToken("new-refresh-token");
        
        assertEquals("new-refresh-token", request.getRefreshToken());
    }
}