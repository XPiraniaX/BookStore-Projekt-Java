package org.example.controller;

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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class BookLoanController {

    private final BookLoanService bookLoanService;
    private final UserService userService;
    private final BookService bookService;

    @PostMapping
    public ResponseEntity<?> createLoan(
            @RequestParam Long userId,
            @RequestParam Long bookId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDate) {
        try {
            BookLoan loan = bookLoanService.createLoan(userId, bookId, dueDate);
            return ResponseEntity.status(HttpStatus.CREATED).body(loan);
        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLoanById(@PathVariable Long id) {
        Optional<BookLoan> loanOptional = bookLoanService.findById(id);
        if (loanOptional.isPresent()) {
            return ResponseEntity.ok(loanOptional.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Loan not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getLoansByUser(@PathVariable Long userId) {
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

    @GetMapping("/book/{bookId}")
    public ResponseEntity<?> getLoansByBook(@PathVariable Long bookId) {
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

    @GetMapping("/active")
    public ResponseEntity<List<BookLoan>> getActiveLoans() {
        List<BookLoan> loans = bookLoanService.findActiveLoans();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/active/user/{userId}")
    public ResponseEntity<?> getActiveLoansForUser(@PathVariable Long userId) {
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

    @GetMapping("/active/book/{bookId}")
    public ResponseEntity<?> getActiveLoansForBook(@PathVariable Long bookId) {
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

    @GetMapping
    public ResponseEntity<List<BookLoan>> getAllLoans() {
        List<BookLoan> loans = bookLoanService.findAllLoans();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<BookLoan>> getOverdueLoans() {
        List<BookLoan> loans = bookLoanService.findOverdueLoans();
        return ResponseEntity.ok(loans);
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<?> returnBook(@PathVariable Long id) {
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