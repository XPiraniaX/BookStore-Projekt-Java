package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.Book;
import org.example.service.BookService;
import org.example.service.UserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private Book book;
    private List<Book> books;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .description("Test Description")
                .quantity(10)
                .availableQuantity(5)
                .build();

        Book book2 = Book.builder()
                .id(2L)
                .title("Another Book")
                .author("Another Author")
                .description("Another Description")
                .quantity(8)
                .availableQuantity(3)
                .build();

        books = Arrays.asList(book, book2);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addBook_Success() throws Exception { // sprawdzenie czy dodaje książkę
        when(bookService.addBook(any(Book.class))).thenReturn(book);

        mockMvc.perform(post("/api/books/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Book")))
                .andExpect(jsonPath("$.author", is("Test Author")));

        verify(bookService).addBook(any(Book.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addBook_Error() throws Exception { // sprawdzenie czy obsługuje błąd podczas dodawania książki
        when(bookService.addBook(any(Book.class))).thenThrow(new IllegalArgumentException("Invalid book data"));

        mockMvc.perform(post("/api/books/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Invalid book data")));

        verify(bookService).addBook(any(Book.class));
    }

    @Test
    void getBookById_Success() throws Exception { // sprawdzenie czy pobiera książkę po ID
        when(bookService.findById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/api/books/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Book")))
                .andExpect(jsonPath("$.author", is("Test Author")));

        verify(bookService).findById(1L);
    }

    @Test
    void getAllBooks() throws Exception { // sprawdzenie czy pobiera wszystkie książki
        when(bookService.findAllBooks()).thenReturn(books);

        mockMvc.perform(get("/api/books/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Book")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Another Book")));

        verify(bookService).findAllBooks();
    }

    @Test
    void getAvailableBooks() throws Exception { // sprawdzenie czy pobiera dostępne książki
        when(bookService.findAvailableBooks()).thenReturn(books);

        mockMvc.perform(get("/api/books/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Book")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Another Book")));

        verify(bookService).findAvailableBooks();
    }

    @Test
    void getBooksByTitle() throws Exception { // sprawdzenie czy wyszukuje książki po tytule
        when(bookService.findByTitle("Test Book")).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books/get_title/Test Book"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Book")));

        verify(bookService).findByTitle("Test Book");
    }

    @Test
    void getBooksByAuthor() throws Exception { // sprawdzenie czy wyszukuje książki po autorze
        when(bookService.findByAuthor("Test Author")).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books/get_author/Test Author"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].author", is("Test Author")));

        verify(bookService).findByAuthor("Test Author");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBook_Success() throws Exception { // sprawdzenie czy aktualizuje książkę
        when(bookService.updateBook(any(Book.class))).thenReturn(book);

        mockMvc.perform(put("/api/books/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Book")));

        verify(bookService).updateBook(any(Book.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBook_NotFound() throws Exception { // sprawdzenie czy obsługuje brak książki podczas aktualizacji
        when(bookService.updateBook(any(Book.class))).thenThrow(new IllegalArgumentException("Book not found"));

        mockMvc.perform(put("/api/books/update/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Book not found")));

        verify(bookService).updateBook(any(Book.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBook_Success() throws Exception { // sprawdzenie czy usuwa książkę
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/delete/1"))
                .andExpect(status().isNoContent());

        verify(bookService).deleteBook(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBook_Error() throws Exception { // sprawdzenie czy obsługuje błąd podczas usuwania książki
        doThrow(new RuntimeException("Error deleting book")).when(bookService).deleteBook(999L);

        mockMvc.perform(delete("/api/books/delete/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Error deleting book")));

        verify(bookService).deleteBook(999L);
    }

    @Test
    void isBookAvailable_True() throws Exception { // sprawdzenie czy książka jest dostępna
        when(bookService.isBookAvailable(1L)).thenReturn(true);

        mockMvc.perform(get("/api/books/available_id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(true)));

        verify(bookService).isBookAvailable(1L);
    }

    @Test
    void isBookAvailable_False() throws Exception { // sprawdzenie czy książka jest niedostępna
        when(bookService.isBookAvailable(2L)).thenReturn(false);

        mockMvc.perform(get("/api/books/available_id/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(false)));

        verify(bookService).isBookAvailable(2L);
    }

    @Test
    void isBookAvailable_NotFound() throws Exception { // sprawdzenie czy obsługuje brak książki przy sprawdzaniu dostępności
        when(bookService.isBookAvailable(999L)).thenThrow(new IllegalArgumentException("Book not found"));

        mockMvc.perform(get("/api/books/available_id/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Book not found")));

        verify(bookService).isBookAvailable(999L);
    }

    @Test
    void getAvailableQuantity_Success() throws Exception { // sprawdzenie czy pobiera dostępną ilość książek
        when(bookService.getAvailableQuantity(1L)).thenReturn(5);

        mockMvc.perform(get("/api/books/quantity/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableQuantity", is(5)));

        verify(bookService).getAvailableQuantity(1L);
    }

    @Test
    void getAvailableQuantity_NotFound() throws Exception { // sprawdzenie czy obsługuje brak książki przy pobieraniu dostępnej ilości
        when(bookService.getAvailableQuantity(999L)).thenThrow(new IllegalArgumentException("Book not found"));

        mockMvc.perform(get("/api/books/quantity/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Book not found")));

        verify(bookService).getAvailableQuantity(999L);
    }
}
