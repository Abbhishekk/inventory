package com.tm.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthConfigTest {

    @Test
    void authConfig_CreatesInstance() {
        AuthConfig config = new AuthConfig();
        
        assertNotNull(config);
    }
}