package com.tm.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "password123");
        AuthResponse response = new AuthResponse("access-token", "refresh-token", "testuser");
        
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void refreshToken_Success() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");
        AuthResponse response = new AuthResponse("new-access-token", "new-refresh-token", "testuser");
        
        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"));
    }

    @Test
    void validateToken_WithQueryParam_Valid() throws Exception {
        when(authService.validateToken("valid-token")).thenReturn(true);
        when(authService.getUsernameFromToken("valid-token")).thenReturn("testuser");

        mockMvc.perform(post("/auth/validate?token=valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void validateToken_WithAuthHeader_Valid() throws Exception {
        when(authService.validateToken("valid-token")).thenReturn(true);
        when(authService.getUsernameFromToken("valid-token")).thenReturn("testuser");

        mockMvc.perform(post("/auth/validate")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    void validateToken_Invalid() throws Exception {
        when(authService.validateToken("invalid-token")).thenReturn(false);

        mockMvc.perform(post("/auth/validate?token=invalid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.username").doesNotExist());
    }

    @Test
    void logout_Success() throws Exception {
        doNothing().when(authService).invalidateToken("token");

        mockMvc.perform(post("/auth/logout?token=token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }

    @Test
    void cleanup_Success() throws Exception {
        doNothing().when(authService).clearAllTokens();

        mockMvc.perform(post("/auth/cleanup"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("All tokens cleared"));
    }

    @Test
    void login_InvalidCredentials_ThrowsException() throws Exception {
        LoginRequest request = new LoginRequest("wronguser", "wrongpass");
        
        when(authService.authenticate(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void refreshToken_InvalidToken_ThrowsException() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("invalid-token");
        
        when(authService.refreshToken(any(RefreshTokenRequest.class)))
                .thenThrow(new RuntimeException("Invalid refresh token"));

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void validateToken_NoTokenProvided_Invalid() throws Exception {
        mockMvc.perform(post("/auth/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    void validateToken_WithAuthHeaderInvalidFormat_Invalid() throws Exception {
        mockMvc.perform(post("/auth/validate")
                .header("Authorization", "InvalidFormat token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    void logout_NoTokenProvided_Success() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }
}