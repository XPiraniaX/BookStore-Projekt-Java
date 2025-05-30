package org.example.service;

import org.example.entity.Book;
import org.example.entity.BookReservation;
import org.example.entity.User;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookReservationServiceTest {

    @Mock
    private BookReservationRepository bookReservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookReservationService bookReservationService;

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
    void createReservation_Success() { // sprawdzenie czy tworzy rezerwację
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookReservationRepository.findByUserAndActive(user, true)).thenReturn(List.of());
        when(bookReservationRepository.save(any(BookReservation.class))).thenReturn(reservation);

        BookReservation result = bookReservationService.createReservation(1L, 1L, expirationDate);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(user, result.getUser());
        assertEquals(book, result.getBook());
        assertTrue(result.isActive());

        verify(bookRepository).save(book);
        assertEquals(2, book.getAvailableQuantity());
    }

    @Test
    void createReservation_UserNotFound() { // sprawdzenie czy obsługuje brak użytkownika podczas tworzenia rezerwacji
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookReservationService.createReservation(1L, 1L, expirationDate)
        );

        assertEquals("User not found", exception.getMessage());
        verify(bookRepository, never()).save(any());
        verify(bookReservationRepository, never()).save(any());
    }

    @Test
    void createReservation_BookNotFound() { // sprawdzenie czy obsługuje brak książki podczas tworzenia rezerwacji
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookReservationService.createReservation(1L, 1L, expirationDate)
        );

        assertEquals("Book not found", exception.getMessage());
        verify(bookRepository, never()).save(any());
        verify(bookReservationRepository, never()).save(any());
    }

    @Test
    void createReservation_BookNotAvailable() { // sprawdzenie czy obsługuje niedostępność książki podczas tworzenia rezerwacji
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
                () -> bookReservationService.createReservation(1L, 1L, expirationDate)
        );

        assertEquals("Book is not available for reservation", exception.getMessage());
        verify(bookRepository, never()).save(any());
        verify(bookReservationRepository, never()).save(any());
    }

    @Test
    void createReservation_UserAlreadyHasActiveReservation() { // sprawdzenie czy obsługuje sytuację gdy użytkownik ma już aktywną rezerwację
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookReservationRepository.findByUserAndActive(user, true)).thenReturn(List.of(reservation));
        when(bookReservationRepository.findByBookAndActive(book, true)).thenReturn(List.of(reservation));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> bookReservationService.createReservation(1L, 1L, expirationDate)
        );

        assertEquals("User already has an active reservation for this book", exception.getMessage());
        verify(bookRepository, never()).save(any());
        verify(bookReservationRepository, never()).save(any());
    }

    @Test
    void findById_Success() { // sprawdzenie czy znajduje rezerwację po ID
        when(bookReservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        Optional<BookReservation> result = bookReservationService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(reservation, result.get());
    }

    @Test
    void findById_NotFound() { // sprawdzenie czy obsługuje brak rezerwacji o podanym ID
        when(bookReservationRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<BookReservation> result = bookReservationService.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void findByUser_Success() { // sprawdzenie czy znajduje rezerwacje użytkownika
        List<BookReservation> reservations = List.of(reservation);
        when(bookReservationRepository.findByUser(user)).thenReturn(reservations);

        List<BookReservation> result = bookReservationService.findByUser(user);

        assertEquals(reservations, result);
    }

    @Test
    void findByBook_Success() { // sprawdzenie czy znajduje rezerwacje książki
        List<BookReservation> reservations = List.of(reservation);
        when(bookReservationRepository.findByBook(book)).thenReturn(reservations);

        List<BookReservation> result = bookReservationService.findByBook(book);

        assertEquals(reservations, result);
    }

    @Test
    void findActiveReservationsByUser_Success() { // sprawdzenie czy znajduje aktywne rezerwacje użytkownika
        List<BookReservation> reservations = List.of(reservation);
        when(bookReservationRepository.findByUserAndActive(user, true)).thenReturn(reservations);

        List<BookReservation> result = bookReservationService.findActiveReservationsByUser(user);

        assertEquals(reservations, result);
    }

    @Test
    void findActiveReservationsByBook_Success() { // sprawdzenie czy znajduje aktywne rezerwacje książki
        List<BookReservation> reservations = List.of(reservation);
        when(bookReservationRepository.findByBookAndActive(book, true)).thenReturn(reservations);

        List<BookReservation> result = bookReservationService.findActiveReservationsByBook(book);

        assertEquals(reservations, result);
    }

    @Test
    void findAllReservations_Success() { // sprawdzenie czy znajduje wszystkie rezerwacje
        List<BookReservation> reservations = List.of(reservation);
        when(bookReservationRepository.findAll()).thenReturn(reservations);

        List<BookReservation> result = bookReservationService.findAllReservations();

        assertEquals(reservations, result);
    }

    @Test
    void findExpiredReservations_Success() { // sprawdzenie czy znajduje wygasłe rezerwacje
        List<BookReservation> reservations = List.of(reservation);
        when(bookReservationRepository.findExpiredReservations(any(LocalDateTime.class))).thenReturn(reservations);

        List<BookReservation> result = bookReservationService.findExpiredReservations();

        assertEquals(reservations, result);
    }

    @Test
    void cancelReservation_Success() { // sprawdzenie czy anuluje rezerwację
        when(bookReservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        bookReservationService.cancelReservation(1L);

        assertFalse(reservation.isActive());
        verify(bookReservationRepository).save(reservation);
        verify(bookRepository).save(book);
        assertEquals(4, book.getAvailableQuantity());
    }

    @Test
    void cancelReservation_NotFound() { // sprawdzenie czy obsługuje brak rezerwacji podczas anulowania
        when(bookReservationRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookReservationService.cancelReservation(1L)
        );

        assertEquals("Reservation not found", exception.getMessage());
        verify(bookReservationRepository, never()).save(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void cancelReservation_NotActive() { // sprawdzenie czy obsługuje próbę anulowania nieaktywnej rezerwacji
        BookReservation inactiveReservation = BookReservation.builder()
                .id(1L)
                .user(user)
                .book(book)
                .reservationDate(now)
                .expirationDate(expirationDate)
                .active(false)
                .build();

        when(bookReservationRepository.findById(1L)).thenReturn(Optional.of(inactiveReservation));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> bookReservationService.cancelReservation(1L)
        );

        assertEquals("Reservation is not active", exception.getMessage());
        verify(bookReservationRepository, never()).save(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void processExpiredReservations_Success() { // sprawdzenie czy przetwarza wygasłe rezerwacje
        BookReservation expiredReservation1 = BookReservation.builder()
                .id(1L)
                .user(user)
                .book(book)
                .reservationDate(now.minusDays(10))
                .expirationDate(now.minusDays(3))
                .active(true)
                .build();

        BookReservation expiredReservation2 = BookReservation.builder()
                .id(2L)
                .user(user)
                .book(book)
                .reservationDate(now.minusDays(8))
                .expirationDate(now.minusDays(1))
                .active(true)
                .build();

        List<BookReservation> expiredReservations = Arrays.asList(expiredReservation1, expiredReservation2);

        when(bookReservationRepository.findExpiredReservations(any(LocalDateTime.class))).thenReturn(expiredReservations);
        when(bookReservationRepository.findById(1L)).thenReturn(Optional.of(expiredReservation1));
        when(bookReservationRepository.findById(2L)).thenReturn(Optional.of(expiredReservation2));

        bookReservationService.processExpiredReservations();

        verify(bookReservationRepository, times(2)).save(any(BookReservation.class));
        verify(bookRepository, times(2)).save(book);
    }

    @Test
    void hasActiveReservation_True() { // sprawdzenie czy wykrywa aktywną rezerwację
        when(bookReservationRepository.findByUserAndActive(user, true)).thenReturn(List.of(reservation));
        when(bookReservationRepository.findByBookAndActive(book, true)).thenReturn(List.of(reservation));

        boolean result = bookReservationService.hasActiveReservation(user, book);

        assertTrue(result);
    }

    @Test
    void hasActiveReservation_False_NoUserReservations() { // sprawdzenie czy obsługuje brak aktywnych rezerwacji użytkownika
        when(bookReservationRepository.findByUserAndActive(user, true)).thenReturn(List.of());

        boolean result = bookReservationService.hasActiveReservation(user, book);

        assertFalse(result);
    }

    @Test
    void hasActiveReservation_False_NoBookReservations() { // sprawdzenie czy obsługuje brak aktywnych rezerwacji książki
        when(bookReservationRepository.findByUserAndActive(user, true)).thenReturn(List.of(reservation));
        when(bookReservationRepository.findByBookAndActive(book, true)).thenReturn(List.of());

        boolean result = bookReservationService.hasActiveReservation(user, book);

        assertFalse(result);
    }

    @Test
    void countActiveReservations_Success() { // sprawdzenie czy poprawnie zlicza aktywne rezerwacje
        when(bookReservationRepository.countActiveReservationsByBook(book)).thenReturn(3L);

        long result = bookReservationService.countActiveReservations(book);

        assertEquals(3L, result);
    }
}
