package org.example.service;

import org.example.entity.Book;
import org.example.entity.BookLoan;
import org.example.entity.BookReservation;
import org.example.entity.User;
import org.example.repository.BookLoanRepository;
import org.example.repository.BookRepository;
import org.example.repository.BookReservationRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookLoanServiceTest {

    @Mock
    private BookLoanRepository bookLoanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookReservationRepository bookReservationRepository;

    @InjectMocks
    private BookLoanService bookLoanService;

    private User user;
    private Book book;
    private BookLoan loan;
    private BookReservation reservation;
    private LocalDateTime now;
    private LocalDateTime dueDate;

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

        reservation = BookReservation.builder()
                .id(1L)
                .user(user)
                .book(book)
                .reservationDate(now.minusDays(1))
                .expirationDate(now.plusDays(6))
                .active(true)
                .build();
    }

    @Test
    void createLoan_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookLoanRepository.findByUserAndReturned(user, false)).thenReturn(Collections.emptyList());
        when(bookReservationRepository.findByUserAndActive(user, true)).thenReturn(Collections.emptyList());
        when(bookReservationRepository.countActiveReservationsByBook(book)).thenReturn(0L);
        when(bookLoanRepository.save(any(BookLoan.class))).thenReturn(loan);

        BookLoan result = bookLoanService.createLoan(1L, 1L, dueDate);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(user, result.getUser());
        assertEquals(book, result.getBook());
        assertFalse(result.isReturned());

        verify(bookRepository).save(book);
        assertEquals(2, book.getAvailableQuantity());
    }

    @Test
    void createLoan_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookLoanService.createLoan(1L, 1L, dueDate)
        );

        assertEquals("User not found", exception.getMessage());
        verify(bookRepository, never()).save(any());
        verify(bookLoanRepository, never()).save(any());
    }

    @Test
    void createLoan_BookNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookLoanService.createLoan(1L, 1L, dueDate)
        );

        assertEquals("Book not found", exception.getMessage());
        verify(bookRepository, never()).save(any());
        verify(bookLoanRepository, never()).save(any());
    }

    @Test
    void createLoan_BookNotAvailable() {
        Book unavailableBook = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .quantity(5)
                .availableQuantity(0)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(unavailableBook));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> bookLoanService.createLoan(1L, 1L, dueDate)
        );

        assertEquals("Book is not available for loan", exception.getMessage());
        verify(bookRepository, never()).save(any());
        verify(bookLoanRepository, never()).save(any());
    }

    @Test
    void createLoan_UserAlreadyHasActiveLoan() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookLoanRepository.findByUserAndReturned(user, false)).thenReturn(List.of(loan));
        when(bookLoanRepository.findByBookAndReturned(book, false)).thenReturn(List.of(loan));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> bookLoanService.createLoan(1L, 1L, dueDate)
        );

        assertEquals("User already has an active loan for this book", exception.getMessage());
        verify(bookRepository, never()).save(any());
        verify(bookLoanRepository, never()).save(any());
    }

    @Test
    void createLoan_BookReservedByOthers() {
        User otherUser = User.builder()
                .id(2L)
                .username("otherUser")
                .email("other@example.com")
                .role(User.Role.USER)
                .build();

        BookReservation otherReservation = BookReservation.builder()
                .id(2L)
                .user(otherUser)
                .book(book)
                .reservationDate(now.minusDays(1))
                .expirationDate(now.plusDays(6))
                .active(true)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookLoanRepository.findByUserAndReturned(user, false)).thenReturn(Collections.emptyList());
        when(bookReservationRepository.findByUserAndActive(user, true)).thenReturn(Collections.emptyList());
        when(bookReservationRepository.countActiveReservationsByBook(book)).thenReturn(1L);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> bookLoanService.createLoan(1L, 1L, dueDate)
        );

        assertEquals("Book is reserved by other users", exception.getMessage());
        verify(bookRepository, never()).save(any());
        verify(bookLoanRepository, never()).save(any());
    }

    @Test
    void createLoan_WithUserReservation() {
        BookReservation userReservation = BookReservation.builder()
                .id(1L)
                .user(user)
                .book(book)
                .reservationDate(now.minusDays(1))
                .expirationDate(now.plusDays(6))
                .active(true)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookLoanRepository.findByUserAndReturned(user, false)).thenReturn(Collections.emptyList());
        when(bookReservationRepository.findByUserAndActive(user, true)).thenReturn(List.of(userReservation));
        when(bookLoanRepository.save(any(BookLoan.class))).thenReturn(loan);

        BookLoan result = bookLoanService.createLoan(1L, 1L, dueDate);

        assertNotNull(result);
        verify(bookReservationRepository).save(userReservation);
        assertFalse(userReservation.isActive());
        verify(bookRepository).save(book);
        assertEquals(2, book.getAvailableQuantity());
    }

    @Test
    void findById_Success() {
        when(bookLoanRepository.findById(1L)).thenReturn(Optional.of(loan));

        Optional<BookLoan> result = bookLoanService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(loan, result.get());
    }

    @Test
    void findById_NotFound() {
        when(bookLoanRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<BookLoan> result = bookLoanService.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void findByUser_Success() {
        List<BookLoan> loans = List.of(loan);
        when(bookLoanRepository.findByUser(user)).thenReturn(loans);

        List<BookLoan> result = bookLoanService.findByUser(user);

        assertEquals(loans, result);
    }

    @Test
    void findByBook_Success() {
        List<BookLoan> loans = List.of(loan);
        when(bookLoanRepository.findByBook(book)).thenReturn(loans);

        List<BookLoan> result = bookLoanService.findByBook(book);

        assertEquals(loans, result);
    }

    @Test
    void findActiveLoans_Success() {
        BookLoan returnedLoan = BookLoan.builder()
                .id(2L)
                .user(user)
                .book(book)
                .loanDate(now.minusDays(20))
                .dueDate(now.minusDays(6))
                .returnDate(now.minusDays(7))
                .returned(true)
                .build();

        List<BookLoan> allLoans = Arrays.asList(loan, returnedLoan);
        when(bookLoanRepository.findAll()).thenReturn(allLoans);

        List<BookLoan> result = bookLoanService.findActiveLoans();

        assertEquals(1, result.size());
        assertEquals(loan, result.get(0));
    }

    @Test
    void findActiveLoansForUser_Success() {
        List<BookLoan> loans = List.of(loan);
        when(bookLoanRepository.findByUserAndReturned(user, false)).thenReturn(loans);

        List<BookLoan> result = bookLoanService.findActiveLoansForUser(user);

        assertEquals(loans, result);
    }

    @Test
    void findActiveLoansForBook_Success() {
        List<BookLoan> loans = List.of(loan);
        when(bookLoanRepository.findByBookAndReturned(book, false)).thenReturn(loans);

        List<BookLoan> result = bookLoanService.findActiveLoansForBook(book);

        assertEquals(loans, result);
    }

    @Test
    void findAllLoans_Success() {
        List<BookLoan> loans = List.of(loan);
        when(bookLoanRepository.findAll()).thenReturn(loans);

        List<BookLoan> result = bookLoanService.findAllLoans();

        assertEquals(loans, result);
    }

    @Test
    void findOverdueLoans_Success() {
        List<BookLoan> loans = List.of(loan);
        when(bookLoanRepository.findOverdueLoans(any(LocalDateTime.class))).thenReturn(loans);

        List<BookLoan> result = bookLoanService.findOverdueLoans();

        assertEquals(loans, result);
    }

    @Test
    void returnBook_Success() {
        when(bookLoanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(bookLoanRepository.save(any(BookLoan.class))).thenReturn(loan);

        BookLoan result = bookLoanService.returnBook(1L);

        assertNotNull(result);
        assertTrue(result.isReturned());
        assertNotNull(result.getReturnDate());
        verify(bookRepository).save(book);
        assertEquals(4, book.getAvailableQuantity());
    }

    @Test
    void returnBook_NotFound() {
        when(bookLoanRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookLoanService.returnBook(1L)
        );

        assertEquals("Loan not found", exception.getMessage());
        verify(bookRepository, never()).save(any());
        verify(bookLoanRepository, never()).save(any());
    }

    @Test
    void returnBook_AlreadyReturned() {
        BookLoan returnedLoan = BookLoan.builder()
                .id(1L)
                .user(user)
                .book(book)
                .loanDate(now.minusDays(10))
                .dueDate(now.plusDays(4))
                .returnDate(now.minusDays(2))
                .returned(true)
                .build();

        when(bookLoanRepository.findById(1L)).thenReturn(Optional.of(returnedLoan));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> bookLoanService.returnBook(1L)
        );

        assertEquals("Book has already been returned", exception.getMessage());
        verify(bookRepository, never()).save(any());
        verify(bookLoanRepository, never()).save(any());
    }

    @Test
    void hasActiveLoan_True() {
        when(bookLoanRepository.findByUserAndReturned(user, false)).thenReturn(List.of(loan));
        when(bookLoanRepository.findByBookAndReturned(book, false)).thenReturn(List.of(loan));

        boolean result = bookLoanService.hasActiveLoan(user, book);

        assertTrue(result);
    }

    @Test
    void hasActiveLoan_False_NoUserLoans() {
        when(bookLoanRepository.findByUserAndReturned(user, false)).thenReturn(Collections.emptyList());

        boolean result = bookLoanService.hasActiveLoan(user, book);

        assertFalse(result);
    }

    @Test
    void hasActiveLoan_False_NoBookLoans() {
        when(bookLoanRepository.findByUserAndReturned(user, false)).thenReturn(List.of(loan));
        when(bookLoanRepository.findByBookAndReturned(book, false)).thenReturn(Collections.emptyList());

        boolean result = bookLoanService.hasActiveLoan(user, book);

        assertFalse(result);
    }

    @Test
    void countActiveLoans_Success() {
        when(bookLoanRepository.countActiveLoans(book)).thenReturn(3L);

        long result = bookLoanService.countActiveLoans(book);

        assertEquals(3L, result);
    }
}
