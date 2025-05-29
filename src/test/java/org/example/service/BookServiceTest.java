package org.example.service;

import org.example.entity.Book;
import org.example.repository.BookLoanRepository;
import org.example.repository.BookRepository;
import org.example.repository.BookReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookLoanRepository bookLoanRepository;

    @Mock
    private BookReservationRepository bookReservationRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .description("Test Description")
                .quantity(5)
                .availableQuantity(3)
                .build();
    }

    @Test
    void addBook_WithAvailableQuantity() {
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book result = bookService.addBook(book);

        assertNotNull(result);
        assertEquals(book, result);
        assertEquals(3, result.getAvailableQuantity());
    }

    @Test
    void addBook_WithoutAvailableQuantity() {
        Book bookWithoutAvailableQuantity = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .description("Test Description")
                .quantity(5)
                .availableQuantity(null)
                .build();

        Book savedBook = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .description("Test Description")
                .quantity(5)
                .availableQuantity(5)
                .build();

        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        Book result = bookService.addBook(bookWithoutAvailableQuantity);

        assertNotNull(result);
        assertEquals(savedBook, result);
        assertEquals(5, result.getAvailableQuantity());
    }

    @Test
    void findById_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Optional<Book> result = bookService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(book, result.get());
    }

    @Test
    void findById_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Book> result = bookService.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void findAllBooks_Success() {
        List<Book> books = Arrays.asList(
                book,
                Book.builder()
                        .id(2L)
                        .title("Another Book")
                        .author("Another Author")
                        .quantity(3)
                        .availableQuantity(2)
                        .build()
        );
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.findAllBooks();

        assertEquals(2, result.size());
        assertEquals(books, result);
    }

    @Test
    void findByTitle_Success() {
        List<Book> books = List.of(book);
        when(bookRepository.findByTitle("Test Book")).thenReturn(books);

        List<Book> result = bookService.findByTitle("Test Book");

        assertEquals(1, result.size());
        assertEquals(books, result);
    }

    @Test
    void findByAuthor_Success() {
        List<Book> books = List.of(book);
        when(bookRepository.findByAuthor("Test Author")).thenReturn(books);

        List<Book> result = bookService.findByAuthor("Test Author");

        assertEquals(1, result.size());
        assertEquals(books, result);
    }

    @Test
    void findAvailableBooks_Success() {
        List<Book> availableBooks = List.of(book);
        when(bookRepository.findAllAvailableBooks()).thenReturn(availableBooks);

        List<Book> result = bookService.findAvailableBooks();

        assertEquals(1, result.size());
        assertEquals(availableBooks, result);
    }

    @Test
    void updateBook_Success() {
        Book updatedBook = Book.builder()
                .id(1L)
                .title("Updated Title")
                .author("Updated Author")
                .description("Updated Description")
                .quantity(5)
                .availableQuantity(3)
                .build();

        when(bookRepository.existsById(1L)).thenReturn(true);
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        Book result = bookService.updateBook(updatedBook);

        assertNotNull(result);
        assertEquals(updatedBook, result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Author", result.getAuthor());
    }

    @Test
    void updateBook_NotFound() {
        when(bookRepository.existsById(1L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.updateBook(book)
        );

        assertEquals("Book not found", exception.getMessage());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void updateBook_AdjustAvailableQuantity() {
        Book bookWithExcessiveAvailableQuantity = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .description("Test Description")
                .quantity(5)
                .availableQuantity(10)
                .build();

        Book adjustedBook = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .description("Test Description")
                .quantity(5)
                .availableQuantity(5)
                .build();

        when(bookRepository.existsById(1L)).thenReturn(true);
        when(bookRepository.save(any(Book.class))).thenReturn(adjustedBook);

        Book result = bookService.updateBook(bookWithExcessiveAvailableQuantity);

        assertNotNull(result);
        assertEquals(adjustedBook, result);
        assertEquals(5, result.getAvailableQuantity());
    }

    @Test
    void deleteBook_Success() {
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository).deleteById(1L);
    }

    @Test
    void isBookAvailable_True() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        boolean result = bookService.isBookAvailable(1L);

        assertTrue(result);
    }

    @Test
    void isBookAvailable_False() {
        Book unavailableBook = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .description("Test Description")
                .quantity(5)
                .availableQuantity(0)
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(unavailableBook));

        boolean result = bookService.isBookAvailable(1L);

        assertFalse(result);
    }

    @Test
    void isBookAvailable_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.isBookAvailable(1L)
        );

        assertEquals("Book not found", exception.getMessage());
    }

    @Test
    void getAvailableQuantity_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        int result = bookService.getAvailableQuantity(1L);

        assertEquals(3, result);
    }

    @Test
    void getAvailableQuantity_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookService.getAvailableQuantity(1L)
        );

        assertEquals("Book not found", exception.getMessage());
    }
}
