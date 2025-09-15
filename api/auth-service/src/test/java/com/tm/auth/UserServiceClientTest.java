package com.tm.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceClientTest {

    @Test
    void validateUserRequest_Constructor_Success() {
        UserServiceClient.ValidateUserRequest request = new UserServiceClient.ValidateUserRequest("testuser", "password");
        
        assertEquals("testuser", request.getUsername());
        assertEquals("password", request.getPassword());
    }

    @Test
    void validateUserRequest_DefaultConstructor_Success() {
        UserServiceClient.ValidateUserRequest request = new UserServiceClient.ValidateUserRequest();
        
        assertNull(request.getUsername());
        assertNull(request.getPassword());
    }

    @Test
    void validateUserRequest_SettersAndGetters_Success() {
        UserServiceClient.ValidateUserRequest request = new UserServiceClient.ValidateUserRequest();
        
        request.setUsername("newuser");
        request.setPassword("newpass");
        
        assertEquals("newuser", request.getUsername());
        assertEquals("newpass", request.getPassword());
    }
}