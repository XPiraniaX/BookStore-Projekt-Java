package org.example.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class BookReservationTest {

    private User user;
    private Book book;
    private LocalDateTime reservationDate;
    private LocalDateTime expirationDate;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .username("testUser")
                .password("password")
                .email("test@example.com")
                .role(User.Role.USER)
                .build();

        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .description("Test Description")
                .quantity(10)
                .availableQuantity(5)
                .build();

        reservationDate = LocalDateTime.now();
        expirationDate = reservationDate.plusDays(7);
    }

    @Test
    public void testBookReservationCreation() { // sprawdzenie czy obiekt BookReservation tworzy się poprawnie
        BookReservation reservation = new BookReservation();
        assertNotNull(reservation);
    }

    @Test
    public void testBookReservationBuilder() { // sprawdzenie czy builder poprawnie tworzy obiekt BookReservation
        BookReservation reservation = BookReservation.builder()
                .id(1L)
                .user(user)
                .book(book)
                .reservationDate(reservationDate)
                .expirationDate(expirationDate)
                .active(true)
                .build();

        assertEquals(1L, reservation.getId());
        assertEquals(user, reservation.getUser());
        assertEquals(book, reservation.getBook());
        assertEquals(reservationDate, reservation.getReservationDate());
        assertEquals(expirationDate, reservation.getExpirationDate());
        assertTrue(reservation.isActive());
    }

    @Test
    public void testBookReservationGettersAndSetters() { // sprawdzenie czy gettery i settery działają poprawnie
        BookReservation reservation = new BookReservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationDate(reservationDate);
        reservation.setExpirationDate(expirationDate);
        reservation.setActive(true);

        assertEquals(1L, reservation.getId());
        assertEquals(user, reservation.getUser());
        assertEquals(book, reservation.getBook());
        assertEquals(reservationDate, reservation.getReservationDate());
        assertEquals(expirationDate, reservation.getExpirationDate());
        assertTrue(reservation.isActive());
    }

    @Test
    public void testBookReservationEqualsAndHashCode() { // sprawdzenie czy metody equals i hashCode działają poprawnie
        BookReservation reservation1 = BookReservation.builder()
                .id(1L)
                .user(user)
                .book(book)
                .reservationDate(reservationDate)
                .expirationDate(expirationDate)
                .active(true)
                .build();

        BookReservation reservation2 = BookReservation.builder()
                .id(1L)
                .user(user)
                .book(book)
                .reservationDate(reservationDate)
                .expirationDate(expirationDate)
                .active(true)
                .build();

        BookReservation reservation3 = BookReservation.builder()
                .id(2L)
                .user(user)
                .book(book)
                .reservationDate(reservationDate)
                .expirationDate(expirationDate.plusDays(1))
                .active(false)
                .build();

        assertEquals(reservation1, reservation2);
        assertEquals(reservation1.hashCode(), reservation2.hashCode());
        assertNotEquals(reservation1, reservation3);
        assertNotEquals(reservation1.hashCode(), reservation3.hashCode());
    }

    @Test
    public void testBookReservationToString() { // sprawdzenie czy metoda toString zwraca poprawny ciąg znaków
        BookReservation reservation = BookReservation.builder()
                .id(1L)
                .user(user)
                .book(book)
                .reservationDate(reservationDate)
                .expirationDate(expirationDate)
                .active(true)
                .build();

        String toString = reservation.toString();

        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("user=" + user.toString()));
        assertTrue(toString.contains("book=" + book.toString()));
        assertTrue(toString.contains("reservationDate=" + reservationDate.toString()));
        assertTrue(toString.contains("expirationDate=" + expirationDate.toString()));
        assertTrue(toString.contains("active=true"));
    }

    @Test
    public void testBookReservationAllArgsConstructor() { // sprawdzenie czy konstruktor z wszystkimi argumentami działa poprawnie
        BookReservation reservation = new BookReservation(1L, user, book, reservationDate, expirationDate, true);

        assertEquals(1L, reservation.getId());
        assertEquals(user, reservation.getUser());
        assertEquals(book, reservation.getBook());
        assertEquals(reservationDate, reservation.getReservationDate());
        assertEquals(expirationDate, reservation.getExpirationDate());
        assertTrue(reservation.isActive());
    }
}
