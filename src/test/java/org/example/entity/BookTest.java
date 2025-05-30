package org.example.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    @Test
    public void testBookCreation() { // sprawdzenie czy obiekt Book tworzy się poprawnie
        Book book = new Book();
        assertNotNull(book);
    }

    @Test
    public void testBookBuilder() { // sprawdzenie czy builder poprawnie tworzy obiekt Book
        Book book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .description("Test Description")
                .quantity(10)
                .availableQuantity(5)
                .build();

        assertEquals(1L, book.getId());
        assertEquals("Test Book", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals("Test Description", book.getDescription());
        assertEquals(10, book.getQuantity());
        assertEquals(5, book.getAvailableQuantity());
    }

    @Test
    public void testBookGettersAndSetters() { // sprawdzenie czy gettery i settery działają poprawnie
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setDescription("Test Description");
        book.setQuantity(10);
        book.setAvailableQuantity(5);

        assertEquals(1L, book.getId());
        assertEquals("Test Book", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals("Test Description", book.getDescription());
        assertEquals(10, book.getQuantity());
        assertEquals(5, book.getAvailableQuantity());
    }

    @Test
    public void testBookEqualsAndHashCode() { // sprawdzenie czy metody equals i hashCode działają poprawnie
        Book book1 = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .description("Test Description")
                .quantity(10)
                .availableQuantity(5)
                .build();

        Book book2 = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .description("Test Description")
                .quantity(10)
                .availableQuantity(5)
                .build();

        Book book3 = Book.builder()
                .id(2L)
                .title("Another Book")
                .author("Another Author")
                .description("Another Description")
                .quantity(20)
                .availableQuantity(15)
                .build();

        assertEquals(book1, book2);
        assertEquals(book1.hashCode(), book2.hashCode());
        assertNotEquals(book1, book3);
        assertNotEquals(book1.hashCode(), book3.hashCode());
    }

    @Test
    public void testBookToString() { // sprawdzenie czy metoda toString zwraca poprawny ciąg znaków
        Book book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .description("Test Description")
                .quantity(10)
                .availableQuantity(5)
                .build();

        String toString = book.toString();

        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("title=Test Book"));
        assertTrue(toString.contains("author=Test Author"));
        assertTrue(toString.contains("description=Test Description"));
        assertTrue(toString.contains("quantity=10"));
        assertTrue(toString.contains("availableQuantity=5"));
    }

    @Test
    public void testBookAllArgsConstructor() { // sprawdzenie czy konstruktor z wszystkimi argumentami działa poprawnie
        Book book = new Book(1L, "Test Book", "Test Author", "Test Description", 10, 5);

        assertEquals(1L, book.getId());
        assertEquals("Test Book", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals("Test Description", book.getDescription());
        assertEquals(10, book.getQuantity());
        assertEquals(5, book.getAvailableQuantity());
    }
}
