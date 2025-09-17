package com.inventoryManagement.auth_service.service;

import com.inventoryManagement.auth_service.entity.UserInfo;
import com.inventoryManagement.auth_service.repository.UserInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserInfoServiceTest {

    private UserInfoRepository repo;
    private PasswordEncoder encoder;
    private UserInfoService service;

    @BeforeEach
    void setup() {
        repo = mock(UserInfoRepository.class);
        encoder = mock(PasswordEncoder.class);
        service = new UserInfoService(repo, encoder);
    }

    @Test
    void loadUserByUsername_found() {
        UserInfo u = new UserInfo(1, "Alice", "a@ex.com", "secret", "ROLE_USER");
        when(repo.findByEmail("a@ex.com")).thenReturn(Optional.of(u));

        UserDetails details = service.loadUserByUsername("a@ex.com");
        assertEquals("a@ex.com", details.getUsername());
        assertEquals("secret", details.getPassword());
    }

    @Test
    void loadUserByUsername_notFound_throws() {
        when(repo.findByEmail("x@ex.com")).thenReturn(Optional.empty());
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                () -> service.loadUserByUsername("x@ex.com"));
    }

    @Test
    void addUser_encodesPassword_andSaves() {
        UserInfo u = new UserInfo(0, "Bob", "b@ex.com", "plain", "ROLE_USER");
        when(encoder.encode("plain")).thenReturn("encoded");

        String res = service.addUser(u);

        assertEquals("User added successfully!", res);
        ArgumentCaptor<UserInfo> captor = ArgumentCaptor.forClass(UserInfo.class);
        verify(repo).save(captor.capture());
        assertEquals("encoded", captor.getValue().getPassword());
    }
}
