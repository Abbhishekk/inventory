package com.tm.auth;

import com.tm.auth.exception.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    
    public AuthResponse authenticate(LoginRequest request) {
        if (request == null || request.getUsername() == null || request.getPassword() == null ||
            request.getUsername().trim().isEmpty() || request.getPassword().trim().isEmpty()) {
            throw new AuthException("Username and password are required");
        }
        
        // Validate against user service
        UserServiceClient.ValidateUserRequest validateRequest = new UserServiceClient.ValidateUserRequest(request.getUsername(), request.getPassword());
        if (!Boolean.TRUE.equals(userServiceClient.validateUser(validateRequest))) {
            throw new AuthException("Invalid credentials");
        }
        
        String accessToken = jwtUtil.generateAccessToken(request.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(request.getUsername());
        
        return new AuthResponse(accessToken, refreshToken, request.getUsername());
    }
    
    public boolean validateToken(String token) {
        return token != null && !blacklistedTokens.contains(token) && jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token);
    }
    
    public String getUsernameFromToken(String token) {
        if (!validateToken(token)) {
            throw new AuthException("Invalid or expired token");
        }
        return jwtUtil.getUsernameFromToken(token);
    }
    
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        
        if (!jwtUtil.validateToken(refreshToken) || jwtUtil.isTokenExpired(refreshToken)) {
            throw new AuthException("Invalid or expired refresh token");
        }
        
        if (!"refresh".equals(jwtUtil.getTokenType(refreshToken))) {
            throw new AuthException("Invalid token type");
        }
        
        if (blacklistedTokens.contains(refreshToken)) {
            throw new AuthException("Refresh token has been revoked");
        }
        
        String username = jwtUtil.getUsernameFromToken(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(username);
        String newRefreshToken = jwtUtil.generateRefreshToken(username);
        
        // Blacklist old refresh token
        blacklistedTokens.add(refreshToken);
        
        return new AuthResponse(newAccessToken, newRefreshToken, username);
    }
    
    public void invalidateToken(String token) {
        if (token != null) {
            blacklistedTokens.add(token);
        }
    }
    
    public void clearAllTokens() {
        blacklistedTokens.clear();
    }
}