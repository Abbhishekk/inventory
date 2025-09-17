package com.inventoryManagement.auth_service.filter;

import com.inventoryManagement.auth_service.service.JwtService;
import com.inventoryManagement.auth_service.service.UserInfoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_setsAuthentication_whenTokenValid() throws Exception {
        JwtService jwt = mock(JwtService.class);
        UserInfoService uds = mock(UserInfoService.class);
        JwtAuthFilter filter = new JwtAuthFilter();
        // inject mocks using reflection since fields are @Autowired
        java.lang.reflect.Field f1 = JwtAuthFilter.class.getDeclaredField("userDetailsService");
        f1.setAccessible(true);
        f1.set(filter, uds);
        java.lang.reflect.Field f2 = JwtAuthFilter.class.getDeclaredField("jwtService");
        f2.setAccessible(true);
        f2.set(filter, jwt);

        String token = "bearer-token";
        when(jwt.extractUsername(token)).thenReturn("u@ex.com");
        when(jwt.validateToken(token)).thenReturn(true);
        UserDetails ud = User.withUsername("u@ex.com").password("pw").roles("USER").build();
        when(uds.loadUserByUsername("u@ex.com")).thenReturn(ud);

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(req, res, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("u@ex.com", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void doFilter_noHeader_leavesContextNull() throws Exception {
        JwtService jwt = mock(JwtService.class);
        UserInfoService uds = mock(UserInfoService.class);
        JwtAuthFilter filter = new JwtAuthFilter();
        java.lang.reflect.Field f1 = JwtAuthFilter.class.getDeclaredField("userDetailsService");
        f1.setAccessible(true);
        f1.set(filter, uds);
        java.lang.reflect.Field f2 = JwtAuthFilter.class.getDeclaredField("jwtService");
        f2.setAccessible(true);
        f2.set(filter, jwt);

        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(req, res, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilter_invalidToken_doesNotSetAuthentication() throws Exception {
        JwtService jwt = mock(JwtService.class);
        UserInfoService uds = mock(UserInfoService.class);
        JwtAuthFilter filter = new JwtAuthFilter();
        java.lang.reflect.Field f1 = JwtAuthFilter.class.getDeclaredField("userDetailsService");
        f1.setAccessible(true);
        f1.set(filter, uds);
        java.lang.reflect.Field f2 = JwtAuthFilter.class.getDeclaredField("jwtService");
        f2.setAccessible(true);
        f2.set(filter, jwt);

        String token = "bad-token";
        when(jwt.extractUsername(token)).thenReturn("u@ex.com");
        when(jwt.validateToken(token)).thenReturn(false);

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(req, res, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
