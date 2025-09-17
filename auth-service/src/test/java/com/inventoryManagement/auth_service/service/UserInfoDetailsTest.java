package com.inventoryManagement.auth_service.service;

import com.inventoryManagement.auth_service.entity.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserInfoDetailsTest {

    @Test
    void userInfoDetails_parsesRoles_andGetters() {
        UserInfo u = new UserInfo(1, "Sam", "sam@ex.com", "pw", "ROLE_USER,ROLE_ADMIN");
        UserInfoDetails details = new UserInfoDetails(u);

        assertEquals("sam@ex.com", details.getUsername());
        assertEquals("pw", details.getPassword());
        Collection<?> auths = details.getAuthorities();
        assertTrue(auths.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(auths.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(details.isAccountNonExpired());
        assertTrue(details.isAccountNonLocked());
        assertTrue(details.isCredentialsNonExpired());
        assertTrue(details.isEnabled());
    }
}
