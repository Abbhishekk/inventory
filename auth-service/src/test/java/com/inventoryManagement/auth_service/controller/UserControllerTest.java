package com.inventoryManagement.auth_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventoryManagement.auth_service.entity.AuthRequest;
import com.inventoryManagement.auth_service.entity.UserInfo;
import com.inventoryManagement.auth_service.service.JwtService;
import com.inventoryManagement.auth_service.service.UserInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;

    private UserInfoService service;

    private JwtService jwtService;

    private AuthenticationManager authenticationManager;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        service = mock(UserInfoService.class);
        jwtService = mock(JwtService.class);
        authenticationManager = mock(AuthenticationManager.class);
        UserController controller = new UserController(service, jwtService, authenticationManager);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void welcome_endpoint_public() throws Exception {
        mockMvc.perform(get("/auth/welcome"))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome this endpoint is not secure"));
    }

    @Test
    void addNewUser_callsService() throws Exception {
        UserInfo u = new UserInfo(0, "N", "n@ex.com", "pw", "ROLE_USER");
        when(service.addUser(any())).thenReturn("User added successfully!");

        mockMvc.perform(post("/auth/addNewUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(u)))
                .andExpect(status().isOk())
                .andExpect(content().string("User added successfully!"));
    }

    @Test
    void generateToken_authenticatesAndReturnsToken() throws Exception {
        AuthRequest req = new AuthRequest("u@ex.com", "pw");
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.generateAccessToken(req.getUsername())).thenReturn("tok123");

        mockMvc.perform(post("/auth/generateToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("tok123"));
    }

    @Test
    void generateToken_badCredentials_fails() throws Exception {
        AuthRequest req = new AuthRequest("u@ex.com", "pw");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad"));

    mockMvc.perform(post("/auth/generateToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(req)))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string("Unauthorized"));
    }
}
