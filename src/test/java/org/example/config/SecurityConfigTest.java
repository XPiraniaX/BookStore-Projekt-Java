package org.example.config;

import org.example.service.UserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SecurityConfigTest {

    @Mock
    private UserDetailsService userDetailsService;

    private SecurityConfig securityConfig;

    @BeforeEach
    public void setUp() {
        securityConfig = new SecurityConfig(userDetailsService);
    }

    @Test
    public void testPasswordEncoder() { // sprawdzenie czy encoder haseł działa poprawnie
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);

        String password = "testPassword";
        String encodedPassword = encoder.encode(password);
        assertNotEquals(password, encodedPassword);
        assertTrue(encoder.matches(password, encodedPassword));
    }

    @Test
    public void testSecurityConfigCreation() { // sprawdzenie czy obiekt SecurityConfig tworzy się poprawnie
        assertNotNull(securityConfig);
    }
}
