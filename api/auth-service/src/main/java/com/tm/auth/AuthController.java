package com.tm.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication and authorization operations")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Refresh JWT token using refresh token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/validate")
    @Operation(summary = "Validate token", description = "Validate JWT token and return user info")
    public ResponseEntity<?> validateToken(@RequestParam(required = false) String token, 
                                         @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String actualToken = extractToken(token, authHeader);
        boolean isValid = authService.validateToken(actualToken);
        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);
        if (isValid) {
            response.put("username", authService.getUsernameFromToken(actualToken));
        }
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user and blacklist JWT token")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> logout(@RequestParam(required = false) String token,
                                  @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String actualToken = extractToken(token, authHeader);
        authService.invalidateToken(actualToken);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/cleanup")
    @Operation(summary = "Cleanup tokens", description = "Clear all blacklisted tokens (admin operation)")
    public ResponseEntity<?> cleanup() {
        authService.clearAllTokens();
        Map<String, String> response = new HashMap<>();
        response.put("message", "All tokens cleared");
        return ResponseEntity.ok(response);
    }
    
    private String extractToken(String token, String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return token;
    }
}