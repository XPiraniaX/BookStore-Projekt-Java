package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.Book;
import org.example.entity.BookReservation;
import org.example.entity.User;
import org.example.service.BookReservationService;
import org.example.service.BookService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class BookReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookReservationService bookReservationService;

    @MockBean
    private UserService userService;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Book book;
    private BookReservation reservation;
    private LocalDateTime now;
    private LocalDateTime expirationDate;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        expirationDate = now.plusDays(7);

        user = User.builder()
                .id(1L)
                .username("testUser")
                .email("test@example.com")
                .role(User.Role.USER)
                .build();

        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .quantity(5)
                .availableQuantity(3)
                .build();

        reservation = BookReservation.builder()
                .id(1L)
                .user(user)
                .book(book)
                .reservationDate(now)
                .expirationDate(expirationDate)
                .active(true)
                .build();
    }

    @Test
    void createReservation_Success() throws Exception {
        when(bookReservationService.createReservation(1L, 1L, expirationDate)).thenReturn(reservation);

        mockMvc.perform(post("/api/reservations/add")
                .param("userId", "1")
                .param("bookId", "1")
                .param("expirationDate", expirationDate.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.user.id", is(1)))
                .andExpect(jsonPath("$.book.id", is(1)))
                .andExpect(jsonPath("$.active", is(true)));

        verify(bookReservationService).createReservation(1L, 1L, expirationDate);
    }

    @Test
    void createReservation_Error() throws Exception {
        when(bookReservationService.createReservation(1L, 1L, expirationDate))
                .thenThrow(new IllegalStateException("Book is not available for reservation"));

        mockMvc.perform(post("/api/reservations/add")
                .param("userId", "1")
                .param("bookId", "1")
                .param("expirationDate", expirationDate.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Book is not available for reservation")));

        verify(bookReservationService).createReservation(1L, 1L, expirationDate);
    }

    @Test
    void getReservationById_Success() throws Exception {
        when(bookReservationService.findById(1L)).thenReturn(Optional.of(reservation));

        mockMvc.perform(get("/api/reservations/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.user.id", is(1)))
                .andExpect(jsonPath("$.book.id", is(1)));

        verify(bookReservationService).findById(1L);
    }

    @Test
    void getReservationById_NotFound() throws Exception {
        when(bookReservationService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reservations/get/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Reservation not found")));

        verify(bookReservationService).findById(999L);
    }

    @Test
    void getReservationsByUser_Success() throws Exception {
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(bookReservationService.findByUser(user)).thenReturn(Collections.singletonList(reservation));

        mockMvc.perform(get("/api/reservations/get_user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].user.id", is(1)))
                .andExpect(jsonPath("$[0].book.id", is(1)));

        verify(userService).findById(1L);
        verify(bookReservationService).findByUser(user);
    }

    @Test
    void getReservationsByUser_UserNotFound() throws Exception {
        when(userService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reservations/get_user/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User not found")));

        verify(userService).findById(999L);
        verify(bookReservationService, never()).findByUser(any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getReservationsByBook_Success() throws Exception {
        when(bookService.findById(1L)).thenReturn(Optional.of(book));
        when(bookReservationService.findByBook(book)).thenReturn(Collections.singletonList(reservation));

        mockMvc.perform(get("/api/reservations/get_book/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].user.id", is(1)))
                .andExpect(jsonPath("$[0].book.id", is(1)));

        verify(bookService).findById(1L);
        verify(bookReservationService).findByBook(book);
    }

    @Test
    void getActiveReservationsByUser_Success() throws Exception {
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(bookReservationService.findActiveReservationsByUser(user)).thenReturn(Collections.singletonList(reservation));

        mockMvc.perform(get("/api/reservations/active_user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].user.id", is(1)))
                .andExpect(jsonPath("$[0].active", is(true)));

        verify(userService).findById(1L);
        verify(bookReservationService).findActiveReservationsByUser(user);
    }

    @Test
    void getActiveReservationsByUser_UserNotFound() throws Exception {
        when(userService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reservations/active_user/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User not found")));

        verify(userService).findById(999L);
        verify(bookReservationService, never()).findActiveReservationsByUser(any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getActiveReservationsByBook_Success() throws Exception {
        when(bookService.findById(1L)).thenReturn(Optional.of(book));
        when(bookReservationService.findActiveReservationsByBook(book)).thenReturn(Collections.singletonList(reservation));

        mockMvc.perform(get("/api/reservations/active_book/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].book.id", is(1)))
                .andExpect(jsonPath("$[0].active", is(true)));

        verify(bookService).findById(1L);
        verify(bookReservationService).findActiveReservationsByBook(book);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getActiveReservationsByBook_BookNotFound() throws Exception {
        when(bookService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reservations/active_book/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Book not found")));

        verify(bookService).findById(999L);
        verify(bookReservationService, never()).findActiveReservationsByBook(any(Book.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllReservations() throws Exception {
        when(bookReservationService.findAllReservations()).thenReturn(Collections.singletonList(reservation));

        mockMvc.perform(get("/api/reservations/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));

        verify(bookReservationService).findAllReservations();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getActiveReservations() throws Exception {
        when(bookReservationService.findAllReservations()).thenReturn(Collections.singletonList(reservation));

        mockMvc.perform(get("/api/reservations/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].active", is(true)));

        verify(bookReservationService).findAllReservations();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getExpiredReservations() throws Exception {
        when(bookReservationService.findExpiredReservations()).thenReturn(Collections.singletonList(reservation));

        mockMvc.perform(get("/api/reservations/expired"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));

        verify(bookReservationService).findExpiredReservations();
    }

    @Test
    void cancelReservation_Success() throws Exception {
        doNothing().when(bookReservationService).cancelReservation(1L);

        mockMvc.perform(post("/api/reservations/cancel/1"))
                .andExpect(status().isOk());

        verify(bookReservationService).cancelReservation(1L);
    }

    @Test
    void cancelReservation_Error() throws Exception {
        doThrow(new IllegalArgumentException("Reservation not found")).when(bookReservationService).cancelReservation(999L);

        mockMvc.perform(post("/api/reservations/cancel/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Reservation not found")));

        verify(bookReservationService).cancelReservation(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void processExpiredReservations_Success() throws Exception {
        doNothing().when(bookReservationService).processExpiredReservations();

        mockMvc.perform(post("/api/reservations/process_expired"))
                .andExpect(status().isOk());

        verify(bookReservationService).processExpiredReservations();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void processExpiredReservations_Error() throws Exception {
        doThrow(new RuntimeException("Error processing expired reservations")).when(bookReservationService).processExpiredReservations();

        mockMvc.perform(post("/api/reservations/process_expired"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Error processing expired reservations")));

        verify(bookReservationService).processExpiredReservations();
    }
}
