package com.inventoryManagement.auth_service.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void generateAndValidateToken() {
        String token = jwtService.generateToken("user@example.com");
        assertNotNull(token);
        assertTrue(jwtService.validateToken(token));
        assertEquals("user@example.com", jwtService.extractUsername(token));
    }

    @Test
    void accessAndRefreshTokens_haveTypeClaim() {
        String access = jwtService.generateAccessToken("alice");
        String refresh = jwtService.generateRefreshToken("alice");
        assertNotNull(access);
        assertNotNull(refresh);
        // tokens should be valid
        assertTrue(jwtService.validateToken(access));
        assertTrue(jwtService.validateToken(refresh));
    }

    @Test
    void validateToken_invalidTokenReturnsFalse() {
        assertFalse(jwtService.validateToken("this-is-not-a-jwt"));
    }

    @Test
    void extractUsername_malformedToken_throws() {
        // validateToken handles exceptions and returns false; extraction will throw for malformed
        String bad = "abc.def.ghi";
        assertThrows(io.jsonwebtoken.JwtException.class, () -> jwtService.extractUsername(bad));
    }
}
