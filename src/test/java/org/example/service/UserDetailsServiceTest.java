package org.example.service;

import org.example.entity.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsService userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testUser")
                .password("password123")
                .email("test@example.com")
                .role(User.Role.USER)
                .build();
    }

    @Test
    void loadUserByUsername_Success() { // sprawdzenie czy poprawnie ładuje dane użytkownika
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testUser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));

        verify(userRepository).findByUsername("testUser");
    }

    @Test
    void loadUserByUsername_UserNotFound() { // sprawdzenie czy obsługuje brak użytkownika o podanej nazwie
        // Arrange
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonExistentUser")
        );

        assertEquals("User not found with username: nonExistentUser", exception.getMessage());
        verify(userRepository).findByUsername("nonExistentUser");
    }

    @Test
    void loadUserByUsername_AdminRole() { // sprawdzenie czy poprawnie ładuje dane administratora
        // Arrange
        User adminUser = User.builder()
                .id(2L)
                .username("adminUser")
                .password("adminPass")
                .email("admin@example.com")
                .role(User.Role.ADMIN)
                .build();

        when(userRepository.findByUsername("adminUser")).thenReturn(Optional.of(adminUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("adminUser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("adminUser", userDetails.getUsername());
        assertEquals("adminPass", userDetails.getPassword());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));

        verify(userRepository).findByUsername("adminUser");
    }
}
