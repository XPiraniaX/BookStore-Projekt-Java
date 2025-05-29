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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private User admin;

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
    }

    @Test
    void registerUser_Success() throws Exception {
        User inputUser = User.builder()
                .username("newUser")
                .password("newPass")
                .email("new@example.com")
                .build();

        User savedUser = User.builder()
                .id(3L)
                .username("newUser")
                .password("encodedPassword")
                .email("new@example.com")
                .role(User.Role.USER)
                .build();

        when(userService.registerUser(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.username", is("newUser")))
                .andExpect(jsonPath("$.email", is("new@example.com")))
                .andExpect(jsonPath("$.role", is("USER")))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userService).registerUser(any(User.class));
    }

    @Test
    void registerUser_Error() throws Exception {
        User inputUser = User.builder()
                .username("existingUser")
                .password("password")
                .email("existing@example.com")
                .build();

        when(userService.registerUser(any(User.class))).thenThrow(new IllegalArgumentException("Username already exists"));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Username already exists")));

        verify(userService).registerUser(any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerAdmin_Success() throws Exception {
        User inputAdmin = User.builder()
                .username("newAdmin")
                .password("adminPass")
                .email("newadmin@example.com")
                .build();

        User savedAdmin = User.builder()
                .id(4L)
                .username("newAdmin")
                .password("encodedPassword")
                .email("newadmin@example.com")
                .role(User.Role.ADMIN)
                .build();

        when(userService.registerUser(any(User.class))).thenReturn(savedAdmin);

        mockMvc.perform(post("/api/auth/register/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputAdmin)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.username", is("newAdmin")))
                .andExpect(jsonPath("$.email", is("newadmin@example.com")))
                .andExpect(jsonPath("$.role", is("ADMIN")))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userService).registerUser(any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerAdmin_Error() throws Exception {
        User inputAdmin = User.builder()
                .username("existingAdmin")
                .password("adminPass")
                .email("existingadmin@example.com")
                .build();

        when(userService.registerUser(any(User.class))).thenThrow(new IllegalArgumentException("Email already exists"));

        mockMvc.perform(post("/api/auth/register/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputAdmin)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Email already exists")));

        verify(userService).registerUser(any(User.class));
    }

    @Test
    void getCurrentUser() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk());
    }
}
