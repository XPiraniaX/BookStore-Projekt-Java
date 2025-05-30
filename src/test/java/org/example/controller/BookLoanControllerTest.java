package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.Book;
import org.example.entity.BookLoan;
import org.example.entity.User;
import org.example.service.BookLoanService;
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

import java.util.Base64;

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
public class BookLoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookLoanService bookLoanService;

    @MockBean
    private UserService userService;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Book book;
    private BookLoan loan;
    private LocalDateTime now;
    private LocalDateTime dueDate;

    private String createBasicAuthHeader() {
        String auth = "testuser:testpassword";
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        return "Basic " + new String(encodedAuth);
    }

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        dueDate = now.plusDays(14);

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

        loan = BookLoan.builder()
                .id(1L)
                .user(user)
                .book(book)
                .loanDate(now)
                .dueDate(dueDate)
                .returned(false)
                .build();
    }

    @Test
    void createLoan_Success() throws Exception { // sprawdzenie czy tworzy się wypożyczenie
        when(bookLoanService.createLoan(1L, 1L, dueDate)).thenReturn(loan);

        mockMvc.perform(post("/api/loans/add")
                .header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader())
                .param("userId", "1")
                .param("bookId", "1")
                .param("dueDate", dueDate.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.user.id", is(1)))
                .andExpect(jsonPath("$.book.id", is(1)))
                .andExpect(jsonPath("$.returned", is(false)));

        verify(bookLoanService).createLoan(1L, 1L, dueDate);
    }

    @Test
    void createLoan_Error() throws Exception { // sprawdzenie czy obsługuje błąd podczas tworzenia wypożyczenia
        when(bookLoanService.createLoan(1L, 1L, dueDate))
                .thenThrow(new IllegalStateException("Book is not available for loan"));

        mockMvc.perform(post("/api/loans/add")
                .header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader())
                .param("userId", "1")
                .param("bookId", "1")
                .param("dueDate", dueDate.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Book is not available for loan")));

        verify(bookLoanService).createLoan(1L, 1L, dueDate);
    }

    @Test
    void getLoanById_Success() throws Exception { // sprawdzenie czy pobiera wypożyczenie po ID
        when(bookLoanService.findById(1L)).thenReturn(Optional.of(loan));

        mockMvc.perform(get("/api/loans/get/1")
                .header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.user.id", is(1)))
                .andExpect(jsonPath("$.book.id", is(1)));

        verify(bookLoanService).findById(1L);
    }

    @Test
    void getLoanById_NotFound() throws Exception { // sprawdzenie czy obsługuje brak wypożyczenia o podanym ID
        when(bookLoanService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/loans/get/999")
                .header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Loan not found")));

        verify(bookLoanService).findById(999L);
    }

    @Test
    void getLoansByUser_Success() throws Exception { // sprawdzenie czy pobiera wypożyczenia użytkownika
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(bookLoanService.findByUser(user)).thenReturn(Collections.singletonList(loan));

        mockMvc.perform(get("/api/loans/get_user/1")
                .header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].user.id", is(1)))
                .andExpect(jsonPath("$[0].book.id", is(1)));

        verify(userService).findById(1L);
        verify(bookLoanService).findByUser(user);
    }

    @Test
    void getLoansByUser_UserNotFound() throws Exception { // sprawdzenie czy obsługuje brak użytkownika przy pobieraniu wypożyczeń
        when(userService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/loans/get_user/999")
                .header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User not found")));

        verify(userService).findById(999L);
        verify(bookLoanService, never()).findByUser(any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getLoansByBook_Success() throws Exception { // sprawdzenie czy pobiera wypożyczenia dla książki
        when(bookService.findById(1L)).thenReturn(Optional.of(book));
        when(bookLoanService.findByBook(book)).thenReturn(Collections.singletonList(loan));

        mockMvc.perform(get("/api/loans/get_book/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].user.id", is(1)))
                .andExpect(jsonPath("$[0].book.id", is(1)));

        verify(bookService).findById(1L);
        verify(bookLoanService).findByBook(book);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getLoansByBook_BookNotFound() throws Exception { // sprawdzenie czy obsługuje brak książki przy pobieraniu wypożyczeń
        when(bookService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/loans/get_book/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Book not found")));

        verify(bookService).findById(999L);
        verify(bookLoanService, never()).findByBook(any(Book.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getActiveLoans() throws Exception { // sprawdzenie czy pobiera aktywne wypożyczenia
        when(bookLoanService.findActiveLoans()).thenReturn(Collections.singletonList(loan));

        mockMvc.perform(get("/api/loans/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].returned", is(false)));

        verify(bookLoanService).findActiveLoans();
    }

    @Test
    void getActiveLoansForUser_Success() throws Exception { // sprawdzenie czy pobiera aktywne wypożyczenia użytkownika
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(bookLoanService.findActiveLoansForUser(user)).thenReturn(Collections.singletonList(loan));

        mockMvc.perform(get("/api/loans/active_user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].user.id", is(1)))
                .andExpect(jsonPath("$[0].returned", is(false)));

        verify(userService).findById(1L);
        verify(bookLoanService).findActiveLoansForUser(user);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getActiveLoansForBook_Success() throws Exception { // sprawdzenie czy pobiera aktywne wypożyczenia dla książki
        when(bookService.findById(1L)).thenReturn(Optional.of(book));
        when(bookLoanService.findActiveLoansForBook(book)).thenReturn(Collections.singletonList(loan));

        mockMvc.perform(get("/api/loans/active_book/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].book.id", is(1)))
                .andExpect(jsonPath("$[0].returned", is(false)));

        verify(bookService).findById(1L);
        verify(bookLoanService).findActiveLoansForBook(book);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllLoans() throws Exception { // sprawdzenie czy pobiera wszystkie wypożyczenia
        when(bookLoanService.findAllLoans()).thenReturn(Collections.singletonList(loan));

        mockMvc.perform(get("/api/loans/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));

        verify(bookLoanService).findAllLoans();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOverdueLoans() throws Exception { // sprawdzenie czy pobiera przeterminowane wypożyczenia
        when(bookLoanService.findOverdueLoans()).thenReturn(Collections.singletonList(loan));

        mockMvc.perform(get("/api/loans/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));

        verify(bookLoanService).findOverdueLoans();
    }

    @Test
    void returnBook_Success() throws Exception { // sprawdzenie czy zwraca wypożyczoną książkę
        BookLoan returnedLoan = BookLoan.builder()
                .id(1L)
                .user(user)
                .book(book)
                .loanDate(now)
                .dueDate(dueDate)
                .returnDate(now.plusDays(7))
                .returned(true)
                .build();

        when(bookLoanService.returnBook(1L)).thenReturn(returnedLoan);

        mockMvc.perform(post("/api/loans/return/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.returned", is(true)))
                .andExpect(jsonPath("$.returnDate").exists());

        verify(bookLoanService).returnBook(1L);
    }

    @Test
    void returnBook_Error() throws Exception { // sprawdzenie czy obsługuje błąd podczas zwrotu książki
        when(bookLoanService.returnBook(999L)).thenThrow(new IllegalArgumentException("Loan not found"));

        mockMvc.perform(post("/api/loans/return/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Loan not found")));

        verify(bookLoanService).returnBook(999L);
    }
}
