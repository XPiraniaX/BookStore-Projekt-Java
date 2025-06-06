package org.example.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testUserCreation() { // sprawdzenie czy obiekt User tworzy się poprawnie
        User user = new User();
        assertNotNull(user);
    }

    @Test
    public void testUserBuilder() { // sprawdzenie czy builder poprawnie tworzy obiekt User
        User user = User.builder()
                .id(1L)
                .username("testUser")
                .password("password")
                .email("test@example.com")
                .role(User.Role.USER)
                .build();

        assertEquals(1L, user.getId());
        assertEquals("testUser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(User.Role.USER, user.getRole());
    }

    @Test
    public void testUserGettersAndSetters() { // sprawdzenie czy gettery i settery działają poprawnie
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setRole(User.Role.USER);

        assertEquals(1L, user.getId());
        assertEquals("testUser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(User.Role.USER, user.getRole());
    }

    @Test
    public void testUserEqualsAndHashCode() { // sprawdzenie czy metody equals i hashCode działają poprawnie
        User user1 = User.builder()
                .id(1L)
                .username("testUser")
                .password("password")
                .email("test@example.com")
                .role(User.Role.USER)
                .build();

        User user2 = User.builder()
                .id(1L)
                .username("testUser")
                .password("password")
                .email("test@example.com")
                .role(User.Role.USER)
                .build();

        User user3 = User.builder()
                .id(2L)
                .username("otherUser")
                .password("otherPassword")
                .email("other@example.com")
                .role(User.Role.ADMIN)
                .build();

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1, user3);
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    public void testUserToString() { // sprawdzenie czy metoda toString zwraca poprawny ciąg znaków
        User user = User.builder()
                .id(1L)
                .username("testUser")
                .password("password")
                .email("test@example.com")
                .role(User.Role.USER)
                .build();

        String toString = user.toString();

        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("username=testUser"));
        assertTrue(toString.contains("password=password"));
        assertTrue(toString.contains("email=test@example.com"));
        assertTrue(toString.contains("role=USER"));
    }

    @Test
    public void testUserAllArgsConstructor() { // sprawdzenie czy konstruktor z wszystkimi argumentami działa poprawnie
        User user = new User(1L, "testUser", "password", "test@example.com", User.Role.USER);

        assertEquals(1L, user.getId());
        assertEquals("testUser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(User.Role.USER, user.getRole());
    }

    @Test
    public void testUserRoleEnum() { // sprawdzenie czy enum Role zawiera poprawne wartości
        assertEquals(2, User.Role.values().length);
        assertEquals(User.Role.USER, User.Role.valueOf("USER"));
        assertEquals(User.Role.ADMIN, User.Role.valueOf("ADMIN"));
    }
}
