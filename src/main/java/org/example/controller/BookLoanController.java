package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.entity.Book;
import org.example.entity.BookLoan;
import org.example.entity.User;
import org.example.service.BookLoanService;
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
@RequestMapping("/api/loans")
@Tag(name = "Book Loan Controller")
@RequiredArgsConstructor
public class BookLoanController {

    private final BookLoanService bookLoanService;
    private final UserService userService;
    private final BookService bookService;

    @PostMapping("/add")
    @Operation(summary = "Create new loan", description = "Adds new loan to database")
    public ResponseEntity<?> createLoan(
            @Parameter(description="ID of the user",required = true) @RequestParam Long userId,
            @Parameter (description="ID of the book",required = true) @RequestParam Long bookId,
            @Parameter (description="Due date",required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDate) {
        try {
            BookLoan loan = bookLoanService.createLoan(userId, bookId, dueDate);
            return ResponseEntity.status(HttpStatus.CREATED).body(loan);
        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Get loan by id", description = "Returns loan with assigned id")
    public ResponseEntity<?> getLoanById(@Parameter(description="ID of the loan",required = true)@PathVariable Long id) {
        Optional<BookLoan> loanOptional = bookLoanService.findById(id);
        if (loanOptional.isPresent()) {
            return ResponseEntity.ok(loanOptional.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Loan not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/get_user/{userId}")
    @Operation(summary = "Get loan by id of assigned user", description = "Returns loan with assigned id of the user")
    public ResponseEntity<?> getLoansByUser(@Parameter(description="ID of the user",required = true) @PathVariable Long userId) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isPresent()) {
            List<BookLoan> loans = bookLoanService.findByUser(userOptional.get());
            return ResponseEntity.ok(loans);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get_book/{bookId}")
    @Operation(summary = "Get loan by id of assigned book", description = "Returns loan with assigned id of the book",security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<?> getLoansByBook(@Parameter(description="ID of the book",required = true) @PathVariable Long bookId) {
        Optional<Book> bookOptional = bookService.findById(bookId);
        if (bookOptional.isPresent()) {
            List<BookLoan> loans = bookLoanService.findByBook(bookOptional.get());
            return ResponseEntity.ok(loans);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Book not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/active")
    @Operation(summary = "Get loans by active status", description = "Returns list of active loans",security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<List<BookLoan>> getActiveLoans() {
        List<BookLoan> loans = bookLoanService.findActiveLoans();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/active_user/{userId}")
    @Operation(summary = "Get active loans for the user with assigend id", description = "Returns list of active loans for the user by id")
    public ResponseEntity<?> getActiveLoansForUser(@Parameter(description="ID of the user",required = true)@PathVariable Long userId) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isPresent()) {
            List<BookLoan> loans = bookLoanService.findActiveLoansForUser(userOptional.get());
            return ResponseEntity.ok(loans);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/active_book/{bookId}")
    @Operation(summary = "Get active loans for the book with assigend id", description = "Returns list of active loans for the book by id",security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<?> getActiveLoansForBook(@Parameter(description="ID of the book",required = true) @PathVariable Long bookId) {
        Optional<Book> bookOptional = bookService.findById(bookId);
        if (bookOptional.isPresent()) {
            List<BookLoan> loans = bookLoanService.findActiveLoansForBook(bookOptional.get());
            return ResponseEntity.ok(loans);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Book not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    @Operation(summary = "Get all loans", description = "Returns list of all loans",security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<List<BookLoan>> getAllLoans() {
        List<BookLoan> loans = bookLoanService.findAllLoans();
        return ResponseEntity.ok(loans);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/overdue")
    @Operation(summary = "Get all overdue loans", description = "Returns list of all overdue loans",security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<List<BookLoan>> getOverdueLoans() {
        List<BookLoan> loans = bookLoanService.findOverdueLoans();
        return ResponseEntity.ok(loans);
    }


    @PostMapping("/return/{id}")
    @Operation(summary = "Returns book by id of the loan", description = "Returns book and deactivates loan by id")
    public ResponseEntity<?> returnBook(@Parameter(description="ID of the loan",required = true) @PathVariable Long id) {
        try {
            BookLoan loan = bookLoanService.returnBook(id);
            return ResponseEntity.ok(loan);
        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}