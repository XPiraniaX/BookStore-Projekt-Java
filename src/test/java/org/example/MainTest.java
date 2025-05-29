package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class MainTest {

    @Test
    void testMainClassCanBeInstantiated() {
        assertDoesNotThrow(() -> new Main());
    }

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
        // The @SpringBootTest annotation takes care of setting up the context
    }
}
