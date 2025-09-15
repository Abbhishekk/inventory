package com.tm.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void generateAccessToken_Success() {
        String token = jwtUtil.generateAccessToken("testuser");
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void generateRefreshToken_Success() {
        String token = jwtUtil.generateRefreshToken("testuser");
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String token = jwtUtil.generateAccessToken("testuser");
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        assertFalse(jwtUtil.validateToken("invalid-token"));
    }

    @Test
    void getUsernameFromToken_ValidToken_ReturnsUsername() {
        String token = jwtUtil.generateAccessToken("testuser");
        assertEquals("testuser", jwtUtil.getUsernameFromToken(token));
    }

    @Test
    void getTokenType_AccessToken_ReturnsAccess() {
        String token = jwtUtil.generateAccessToken("testuser");
        assertEquals("access", jwtUtil.getTokenType(token));
    }

    @Test
    void getTokenType_RefreshToken_ReturnsRefresh() {
        String token = jwtUtil.generateRefreshToken("testuser");
        assertEquals("refresh", jwtUtil.getTokenType(token));
    }

    @Test
    void isTokenExpired_FreshToken_ReturnsFalse() {
        String token = jwtUtil.generateAccessToken("testuser");
        assertFalse(jwtUtil.isTokenExpired(token));
    }
}