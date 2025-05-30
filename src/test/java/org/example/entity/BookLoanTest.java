package org.example.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class BookLoanTest {

    private User user;
    private Book book;
    private LocalDateTime loanDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;

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

        loanDate = LocalDateTime.now();
        dueDate = loanDate.plusDays(14);
        returnDate = loanDate.plusDays(10);
    }

    @Test
    public void testBookLoanCreation() { // sprawdzenie czy obiekt BookLoan tworzy się poprawnie
        BookLoan bookLoan = new BookLoan();
        assertNotNull(bookLoan);
    }

    @Test
    public void testBookLoanBuilder() { // sprawdzenie czy builder poprawnie tworzy obiekt BookLoan
        BookLoan bookLoan = BookLoan.builder()
                .id(1L)
                .user(user)
                .book(book)
                .loanDate(loanDate)
                .dueDate(dueDate)
                .returnDate(returnDate)
                .returned(true)
                .build();

        assertEquals(1L, bookLoan.getId());
        assertEquals(user, bookLoan.getUser());
        assertEquals(book, bookLoan.getBook());
        assertEquals(loanDate, bookLoan.getLoanDate());
        assertEquals(dueDate, bookLoan.getDueDate());
        assertEquals(returnDate, bookLoan.getReturnDate());
        assertTrue(bookLoan.isReturned());
    }

    @Test
    public void testBookLoanGettersAndSetters() { // sprawdzenie czy gettery i settery działają poprawnie
        BookLoan bookLoan = new BookLoan();
        bookLoan.setId(1L);
        bookLoan.setUser(user);
        bookLoan.setBook(book);
        bookLoan.setLoanDate(loanDate);
        bookLoan.setDueDate(dueDate);
        bookLoan.setReturnDate(returnDate);
        bookLoan.setReturned(true);

        assertEquals(1L, bookLoan.getId());
        assertEquals(user, bookLoan.getUser());
        assertEquals(book, bookLoan.getBook());
        assertEquals(loanDate, bookLoan.getLoanDate());
        assertEquals(dueDate, bookLoan.getDueDate());
        assertEquals(returnDate, bookLoan.getReturnDate());
        assertTrue(bookLoan.isReturned());
    }

    @Test
    public void testBookLoanEqualsAndHashCode() { // sprawdzenie czy metody equals i hashCode działają poprawnie
        BookLoan bookLoan1 = BookLoan.builder()
                .id(1L)
                .user(user)
                .book(book)
                .loanDate(loanDate)
                .dueDate(dueDate)
                .returnDate(returnDate)
                .returned(true)
                .build();

        BookLoan bookLoan2 = BookLoan.builder()
                .id(1L)
                .user(user)
                .book(book)
                .loanDate(loanDate)
                .dueDate(dueDate)
                .returnDate(returnDate)
                .returned(true)
                .build();

        BookLoan bookLoan3 = BookLoan.builder()
                .id(2L)
                .user(user)
                .book(book)
                .loanDate(loanDate)
                .dueDate(dueDate)
                .returnDate(null)
                .returned(false)
                .build();

        assertEquals(bookLoan1, bookLoan2);
        assertEquals(bookLoan1.hashCode(), bookLoan2.hashCode());
        assertNotEquals(bookLoan1, bookLoan3);
        assertNotEquals(bookLoan1.hashCode(), bookLoan3.hashCode());
    }

    @Test
    public void testBookLoanToString() { // sprawdzenie czy metoda toString zwraca poprawny ciąg znaków
        BookLoan bookLoan = BookLoan.builder()
                .id(1L)
                .user(user)
                .book(book)
                .loanDate(loanDate)
                .dueDate(dueDate)
                .returnDate(returnDate)
                .returned(true)
                .build();

        String toString = bookLoan.toString();

        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("user=" + user.toString()));
        assertTrue(toString.contains("book=" + book.toString()));
        assertTrue(toString.contains("loanDate=" + loanDate.toString()));
        assertTrue(toString.contains("dueDate=" + dueDate.toString()));
        assertTrue(toString.contains("returnDate=" + returnDate.toString()));
        assertTrue(toString.contains("returned=true"));
    }

    @Test
    public void testBookLoanAllArgsConstructor() { // sprawdzenie czy konstruktor z wszystkimi argumentami działa poprawnie
        BookLoan bookLoan = new BookLoan(1L, user, book, loanDate, dueDate, returnDate, true);

        assertEquals(1L, bookLoan.getId());
        assertEquals(user, bookLoan.getUser());
        assertEquals(book, bookLoan.getBook());
        assertEquals(loanDate, bookLoan.getLoanDate());
        assertEquals(dueDate, bookLoan.getDueDate());
        assertEquals(returnDate, bookLoan.getReturnDate());
        assertTrue(bookLoan.isReturned());
    }
}
