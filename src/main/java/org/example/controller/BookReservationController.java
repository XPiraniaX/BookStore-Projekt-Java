package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.entity.Book;
import org.example.entity.BookReservation;
import org.example.entity.User;
import org.example.service.BookReservationService;
import org.example.service.BookService;
import org.example.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class BookReservationController {

    private final BookReservationService bookReservationService;
    private final UserService userService;
    private final BookService bookService;

    @PostMapping
    public ResponseEntity<?> createReservation(
            @RequestParam Long userId,
            @RequestParam Long bookId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expirationDate) {
        try {
            BookReservation reservation = bookReservationService.createReservation(userId, bookId, expirationDate);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        Optional<BookReservation> reservationOptional = bookReservationService.findById(id);
        if (reservationOptional.isPresent()) {
            return ResponseEntity.ok(reservationOptional.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Reservation not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReservationsByUser(@PathVariable Long userId) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isPresent()) {
            List<BookReservation> reservations = bookReservationService.findByUser(userOptional.get());
            return ResponseEntity.ok(reservations);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<?> getReservationsByBook(@PathVariable Long bookId) {
        Optional<Book> bookOptional = bookService.findById(bookId);
        if (bookOptional.isPresent()) {
            List<BookReservation> reservations = bookReservationService.findByBook(bookOptional.get());
            return ResponseEntity.ok(reservations);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Book not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/active/user/{userId}")
    public ResponseEntity<?> getActiveReservationsByUser(@PathVariable Long userId) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isPresent()) {
            List<BookReservation> reservations = bookReservationService.findActiveReservationsByUser(userOptional.get());
            return ResponseEntity.ok(reservations);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/active/book/{bookId}")
    public ResponseEntity<?> getActiveReservationsByBook(@PathVariable Long bookId) {
        Optional<Book> bookOptional = bookService.findById(bookId);
        if (bookOptional.isPresent()) {
            List<BookReservation> reservations = bookReservationService.findActiveReservationsByBook(bookOptional.get());
            return ResponseEntity.ok(reservations);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Book not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<BookReservation>> getAllReservations() {
        List<BookReservation> reservations = bookReservationService.findAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/expired")
    public ResponseEntity<List<BookReservation>> getExpiredReservations() {
        List<BookReservation> reservations = bookReservationService.findExpiredReservations();
        return ResponseEntity.ok(reservations);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        try {
            bookReservationService.cancelReservation(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/process-expired")
    public ResponseEntity<?> processExpiredReservations() {
        try {
            bookReservationService.processExpiredReservations();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}