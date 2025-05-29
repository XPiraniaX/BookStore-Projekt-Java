package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.entity.Book;
import org.example.entity.BookLoan;
import org.example.entity.BookReservation;
import org.example.entity.User;
import org.example.service.BookReservationService;
import org.example.service.BookService;
import org.example.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Book Reservation Controller")
@RequiredArgsConstructor
public class BookReservationController {

    private final BookReservationService bookReservationService;
    private final UserService userService;
    private final BookService bookService;

    @PostMapping("/add")
    @Operation(summary = "Create new reservation", description = "Adds new reservation to database")
    public ResponseEntity<?> createReservation(
            @Parameter(description="ID of the user",required = true) @RequestParam Long userId,
            @Parameter (description="ID of the book",required = true) @RequestParam Long bookId,
            @Parameter (description="Reservation expiration date",required = true)@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expirationDate) {
        try {
            BookReservation reservation = bookReservationService.createReservation(userId, bookId, expirationDate);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Get reservation by id", description = "Returns reservation with assigned id")
    public ResponseEntity<?> getReservationById(@Parameter(description="ID of the reservation",required = true)@PathVariable Long id) {
        Optional<BookReservation> reservationOptional = bookReservationService.findById(id);
        if (reservationOptional.isPresent()) {
            return ResponseEntity.ok(reservationOptional.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Reservation not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/get_user/{userId}")
    @Operation(summary = "Get reservation by id of assigned user", description = "Returns reservation with assigned id of the user")
    public ResponseEntity<?> getReservationsByUser(@Parameter(description="ID of the user",required = true) @PathVariable Long userId) {
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
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get_book/{bookId}")
    @Operation(summary = "Get reservation by id of assigned book", description = "Returns reservation with assigned id of the book",security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<?> getReservationsByBook(@Parameter(description="ID of the book",required = true)@PathVariable Long bookId) {
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

    @GetMapping("/active_user/{userId}")
    @Operation(summary = "Get active reservations for the user with assigend id", description = "Returns list of active reservations for the user by id")
    public ResponseEntity<?> getActiveReservationsByUser(@Parameter(description="ID of the user",required = true) @PathVariable Long userId) {
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/active_book/{bookId}")
    @Operation(summary = "Get active reservations for the book with assigend id", description = "Returns list of active reservations for the book by id",security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<?> getActiveReservationsByBook(@Parameter(description="ID of the book",required = true) @PathVariable Long bookId) {
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    @Operation(summary = "Get all reservations", description = "Returns list of all reservations",security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<List<BookReservation>> getAllReservations() {
        List<BookReservation> reservation = bookReservationService.findAllReservations();
        return ResponseEntity.ok(reservation);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/active")
    @Operation(summary = "Get all active reservations", description = "Returns list of active reservations",security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<List<BookReservation>> getActiveReservations() {
        List<BookReservation> reservations = bookReservationService.findAllReservations().stream()
                .filter(BookReservation::isActive)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(reservations);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/expired")
    @Operation(summary = "Get all expired reservations", description = "Returns list of all expired reservations",security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<List<BookReservation>> getExpiredReservations() {
        List<BookReservation> reservations = bookReservationService.findExpiredReservations();
        return ResponseEntity.ok(reservations);
    }

    @PostMapping("/cancel/{id}")
    @Operation(summary = "Cancels reservation for the book by id of the reservation", description = "Sets the reservation as inactive by id")
    public ResponseEntity<?> cancelReservation(@Parameter(description="ID of the reservation",required = true) @PathVariable Long id) {
        try {
            bookReservationService.cancelReservation(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/process_expired")
    @Operation(summary = "Cancels expired reservations", description = "Using cancel system on overdue reservations",security = @SecurityRequirement(name = "basicAuth"))
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
