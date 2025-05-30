package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.User;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private User admin;
    private List<User> users;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testUser")
                .password("password123")
                .email("test@example.com")
                .role(User.Role.USER)
                .build();

        admin = User.builder()
                .id(2L)
                .username("adminUser")
                .password("adminPass")
                .email("admin@example.com")
                .role(User.Role.ADMIN)
                .build();

        users = Arrays.asList(user, admin);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_Success() throws Exception { // sprawdzenie czy pobiera użytkownika po ID
        when(userService.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("testUser")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.role", is("USER")))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userService).findById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_NotFound() throws Exception { // sprawdzenie czy obsługuje brak użytkownika o podanym ID
        when(userService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/get/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User not found")));

        verify(userService).findById(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserByUsername_Success() throws Exception { // sprawdzenie czy pobiera użytkownika po nazwie użytkownika
        when(userService.findByUsername("testUser")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/get_username/testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("testUser")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.role", is("USER")))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userService).findByUsername("testUser");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserByUsername_NotFound() throws Exception { // sprawdzenie czy obsługuje brak użytkownika o podanej nazwie
        when(userService.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/get_username/nonExistentUser"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User not found")));

        verify(userService).findByUsername("nonExistentUser");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers() throws Exception { // sprawdzenie czy pobiera wszystkich użytkowników
        when(userService.findAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].username", is("testUser")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].username", is("adminUser")))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[1].password").doesNotExist());

        verify(userService).findAllUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_Success() throws Exception { // sprawdzenie czy aktualizuje użytkownika
        User updatedUser = User.builder()
                .id(1L)
                .username("updatedUser")
                .password("newPassword")
                .email("updated@example.com")
                .role(User.Role.USER)
                .build();

        when(userService.updateUser(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("updatedUser")))
                .andExpect(jsonPath("$.email", is("updated@example.com")))
                .andExpect(jsonPath("$.role", is("USER")))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userService).updateUser(any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_Success() throws Exception { // sprawdzenie czy usuwa użytkownika
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/delete/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_Error() throws Exception { // sprawdzenie czy obsługuje błąd podczas usuwania użytkownika
        doThrow(new RuntimeException("Error deleting user")).when(userService).deleteUser(999L);

        mockMvc.perform(delete("/api/users/delete/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Error deleting user")));

        verify(userService).deleteUser(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void existsByUsername_True() throws Exception { // sprawdzenie czy istnieje użytkownik o podanej nazwie
        when(userService.existsByUsername("testUser")).thenReturn(true);

        mockMvc.perform(get("/api/users/exists_username/testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists", is(true)));

        verify(userService).existsByUsername("testUser");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void existsByUsername_False() throws Exception { // sprawdzenie czy nie istnieje użytkownik o podanej nazwie
        when(userService.existsByUsername("nonExistentUser")).thenReturn(false);

        mockMvc.perform(get("/api/users/exists_username/nonExistentUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists", is(false)));

        verify(userService).existsByUsername("nonExistentUser");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void existsByEmail_True() throws Exception { // sprawdzenie czy istnieje użytkownik o podanym adresie email
        when(userService.existsByEmail("test@example.com")).thenReturn(true);

        mockMvc.perform(get("/api/users/exists_email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists", is(true)));

        verify(userService).existsByEmail("test@example.com");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void existsByEmail_False() throws Exception { // sprawdzenie czy nie istnieje użytkownik o podanym adresie email
        when(userService.existsByEmail("nonexistent@example.com")).thenReturn(false);

        mockMvc.perform(get("/api/users/exists_email/nonexistent@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists", is(false)));

        verify(userService).existsByEmail("nonexistent@example.com");
    }
}
