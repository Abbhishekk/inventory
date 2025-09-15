package com.tm.auth;

import com.tm.auth.exception.AuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService.clearAllTokens();
    }

    @Test
    void authenticate_ValidCredentials_Success() {
        LoginRequest request = new LoginRequest("testuser", "password123");
        when(userServiceClient.validateUser(any(UserServiceClient.ValidateUserRequest.class))).thenReturn(true);
        when(jwtUtil.generateAccessToken("testuser")).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken("testuser")).thenReturn("refresh-token");

        AuthResponse response = authService.authenticate(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("testuser", response.getUsername());
    }

    @Test
    void authenticate_InvalidCredentials_ThrowsException() {
        LoginRequest request = new LoginRequest("wronguser", "wrongpass");
        when(userServiceClient.validateUser(any(UserServiceClient.ValidateUserRequest.class))).thenReturn(false);

        assertThrows(AuthException.class, () -> authService.authenticate(request));
    }

    @Test
    void authenticate_NullRequest_ThrowsException() {
        assertThrows(AuthException.class, () -> authService.authenticate(null));
    }

    @Test
    void authenticate_EmptyUsername_ThrowsException() {
        LoginRequest request = new LoginRequest("", "password123");
        assertThrows(AuthException.class, () -> authService.authenticate(request));
    }

    @Test
    void authenticate_EmptyPassword_ThrowsException() {
        LoginRequest request = new LoginRequest("testuser", "");
        assertThrows(AuthException.class, () -> authService.authenticate(request));
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        when(jwtUtil.validateToken("valid-token")).thenReturn(true);
        when(jwtUtil.isTokenExpired("valid-token")).thenReturn(false);

        assertTrue(authService.validateToken("valid-token"));
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        when(jwtUtil.validateToken("invalid-token")).thenReturn(false);

        assertFalse(authService.validateToken("invalid-token"));
    }

    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        when(jwtUtil.validateToken("expired-token")).thenReturn(true);
        when(jwtUtil.isTokenExpired("expired-token")).thenReturn(true);

        assertFalse(authService.validateToken("expired-token"));
    }

    @Test
    void validateToken_BlacklistedToken_ReturnsFalse() {
        authService.invalidateToken("blacklisted-token");

        assertFalse(authService.validateToken("blacklisted-token"));
    }

    @Test
    void validateToken_NullToken_ReturnsFalse() {
        assertFalse(authService.validateToken(null));
    }

    @Test
    void getUsernameFromToken_ValidToken_ReturnsUsername() {
        when(jwtUtil.validateToken("valid-token")).thenReturn(true);
        when(jwtUtil.isTokenExpired("valid-token")).thenReturn(false);
        when(jwtUtil.getUsernameFromToken("valid-token")).thenReturn("testuser");

        assertEquals("testuser", authService.getUsernameFromToken("valid-token"));
    }

    @Test
    void getUsernameFromToken_InvalidToken_ThrowsException() {
        when(jwtUtil.validateToken("invalid-token")).thenReturn(false);

        assertThrows(AuthException.class, () -> authService.getUsernameFromToken("invalid-token"));
    }

    @Test
    void refreshToken_ValidRefreshToken_Success() {
        RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");
        when(jwtUtil.validateToken("refresh-token")).thenReturn(true);
        when(jwtUtil.isTokenExpired("refresh-token")).thenReturn(false);
        when(jwtUtil.getTokenType("refresh-token")).thenReturn("refresh");
        when(jwtUtil.getUsernameFromToken("refresh-token")).thenReturn("testuser");
        when(jwtUtil.generateAccessToken("testuser")).thenReturn("new-access-token");
        when(jwtUtil.generateRefreshToken("testuser")).thenReturn("new-refresh-token");

        AuthResponse response = authService.refreshToken(request);

        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        assertEquals("testuser", response.getUsername());
    }

    @Test
    void refreshToken_InvalidToken_ThrowsException() {
        RefreshTokenRequest request = new RefreshTokenRequest("invalid-token");
        when(jwtUtil.validateToken("invalid-token")).thenReturn(false);

        assertThrows(AuthException.class, () -> authService.refreshToken(request));
    }

    @Test
    void refreshToken_WrongTokenType_ThrowsException() {
        RefreshTokenRequest request = new RefreshTokenRequest("access-token");
        when(jwtUtil.validateToken("access-token")).thenReturn(true);
        when(jwtUtil.isTokenExpired("access-token")).thenReturn(false);
        when(jwtUtil.getTokenType("access-token")).thenReturn("access");

        assertThrows(AuthException.class, () -> authService.refreshToken(request));
    }

    @Test
    void invalidateToken_Success() {
        authService.invalidateToken("token");
        
        assertFalse(authService.validateToken("token"));
    }

    @Test
    void clearAllTokens_Success() {
        authService.invalidateToken("token1");
        authService.invalidateToken("token2");
        
        authService.clearAllTokens();
        
        when(jwtUtil.validateToken("token1")).thenReturn(true);
        when(jwtUtil.isTokenExpired("token1")).thenReturn(false);
        
        assertTrue(authService.validateToken("token1"));
    }

    @Test
    void refreshToken_BlacklistedToken_ThrowsException() {
        RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");
        authService.invalidateToken("refresh-token");
        
        when(jwtUtil.validateToken("refresh-token")).thenReturn(true);
        when(jwtUtil.isTokenExpired("refresh-token")).thenReturn(false);
        when(jwtUtil.getTokenType("refresh-token")).thenReturn("refresh");

        assertThrows(AuthException.class, () -> authService.refreshToken(request));
    }

    @Test
    void refreshToken_ExpiredToken_ThrowsException() {
        RefreshTokenRequest request = new RefreshTokenRequest("expired-refresh-token");
        
        when(jwtUtil.validateToken("expired-refresh-token")).thenReturn(true);
        when(jwtUtil.isTokenExpired("expired-refresh-token")).thenReturn(true);

        assertThrows(AuthException.class, () -> authService.refreshToken(request));
    }

    @Test
    void refreshToken_NullRequest_ThrowsException() {
        assertThrows(NullPointerException.class, () -> authService.refreshToken(null));
    }

    @Test
    void invalidateToken_NullToken_DoesNothing() {
        assertDoesNotThrow(() -> authService.invalidateToken(null));
    }

    @Test
    void authenticate_NullUsername_ThrowsException() {
        LoginRequest request = new LoginRequest(null, "password123");
        assertThrows(AuthException.class, () -> authService.authenticate(request));
    }

    @Test
    void authenticate_NullPassword_ThrowsException() {
        LoginRequest request = new LoginRequest("testuser", null);
        assertThrows(AuthException.class, () -> authService.authenticate(request));
    }

    @Test
    void authenticate_WhitespaceUsername_ThrowsException() {
        LoginRequest request = new LoginRequest("   ", "password123");
        assertThrows(AuthException.class, () -> authService.authenticate(request));
    }

    @Test
    void authenticate_WhitespacePassword_ThrowsException() {
        LoginRequest request = new LoginRequest("testuser", "   ");
        assertThrows(AuthException.class, () -> authService.authenticate(request));
    }
}