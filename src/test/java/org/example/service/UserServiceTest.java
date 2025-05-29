package org.example.service;

import org.example.entity.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

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
    void registerUser_Success() {
        when(userRepository.existsByUsername("testUser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(user);

        assertNotNull(result);
        assertEquals(user, result);
        assertEquals("encodedPassword", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void registerUser_UsernameExists() {
        when(userRepository.existsByUsername("testUser")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(user)
        );

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_EmailExists() {
        when(userRepository.existsByUsername("testUser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(user)
        );

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_NullRole() {
        User userWithNullRole = User.builder()
                .id(1L)
                .username("testUser")
                .password("password123")
                .email("test@example.com")
                .role(null)
                .build();

        when(userRepository.existsByUsername("testUser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(userWithNullRole);

        User result = userService.registerUser(userWithNullRole);

        assertNotNull(result);
        assertEquals(User.Role.USER, userWithNullRole.getRole());
    }

    @Test
    void findByUsername_Success() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("testUser");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void findByUsername_NotFound() {
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername("nonExistentUser");

        assertFalse(result.isPresent());
    }

    @Test
    void findById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void findById_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void findAllUsers_Success() {
        User anotherUser = User.builder()
                .id(2L)
                .username("anotherUser")
                .password("password456")
                .email("another@example.com")
                .role(User.Role.USER)
                .build();

        List<User> users = Arrays.asList(user, anotherUser);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAllUsers();

        assertEquals(2, result.size());
        assertEquals(users, result);
    }

    @Test
    void updateUser_WithNewPassword() {
        User userToUpdate = User.builder()
                .id(1L)
                .username("updatedUser")
                .password("newPassword")
                .email("updated@example.com")
                .role(User.Role.ADMIN)
                .build();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(userToUpdate);

        User result = userService.updateUser(userToUpdate);

        assertNotNull(result);
        assertEquals(userToUpdate, result);
        assertEquals("encodedNewPassword", userToUpdate.getPassword());
    }

    @Test
    void updateUser_WithEmptyPassword() {
        User userToUpdate = User.builder()
                .id(1L)
                .username("updatedUser")
                .password("")
                .email("updated@example.com")
                .role(User.Role.ADMIN)
                .build();

        User existingUser = User.builder()
                .id(1L)
                .username("testUser")
                .password("encodedOldPassword")
                .email("test@example.com")
                .role(User.Role.USER)
                .build();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(userToUpdate);

        User result = userService.updateUser(userToUpdate);

        assertNotNull(result);
        assertEquals(userToUpdate, result);
        assertEquals("encodedOldPassword", userToUpdate.getPassword());
    }

    @Test
    void updateUser_WithNullPassword() {
        User userToUpdate = User.builder()
                .id(1L)
                .username("updatedUser")
                .password(null)
                .email("updated@example.com")
                .role(User.Role.ADMIN)
                .build();

        User existingUser = User.builder()
                .id(1L)
                .username("testUser")
                .password("encodedOldPassword")
                .email("test@example.com")
                .role(User.Role.USER)
                .build();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(userToUpdate);

        User result = userService.updateUser(userToUpdate);

        assertNotNull(result);
        assertEquals(userToUpdate, result);
        assertEquals("encodedOldPassword", userToUpdate.getPassword());
    }

    @Test
    void updateUser_NotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(user)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_Success() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void existsByUsername_True() {
        when(userRepository.existsByUsername("testUser")).thenReturn(true);

        boolean result = userService.existsByUsername("testUser");

        assertTrue(result);
    }

    @Test
    void existsByUsername_False() {
        when(userRepository.existsByUsername("nonExistentUser")).thenReturn(false);

        boolean result = userService.existsByUsername("nonExistentUser");

        assertFalse(result);
    }

    @Test
    void existsByEmail_True() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = userService.existsByEmail("test@example.com");

        assertTrue(result);
    }

    @Test
    void existsByEmail_False() {
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        boolean result = userService.existsByEmail("nonexistent@example.com");

        assertFalse(result);
    }
}
