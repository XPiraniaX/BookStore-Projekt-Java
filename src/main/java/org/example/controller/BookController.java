package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.entity.Book;
import org.example.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Book Controller")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    @Operation(summary = "Create new book", description = "Adds new book to database",security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<?> addBook(@Valid @RequestBody Book book) {
        try {
            Book savedBook = bookService.addBook(book);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/get/{id}")
    @Operation (summary = "Get book by id", description = "Returns book with assigned id")
    public ResponseEntity<?> getBookById(@Parameter(description="ID of the book",required = true)@PathVariable Long id) {
        Optional<Book> bookOptional = bookService.findById(id);
        if (bookOptional.isPresent()) {
            return ResponseEntity.ok(bookOptional.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Book not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/all")
    @Operation (summary = "Get all books", description = "Returns list of all books in database")
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.findAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/available")
    @Operation (summary = "Get available book", description = "Returns list of all available books in database")
    public ResponseEntity<List<Book>> getAvailableBooks() {
        List<Book> books = bookService.findAvailableBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/get_title/{title}")
    @Operation (summary = "Get books by title", description = "Returns list of all books with matching title")
    public ResponseEntity<List<Book>> getBooksByTitle(@Parameter (description="Title of the book",required = true)@PathVariable String title) {
        List<Book> books = bookService.findByTitle(title);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/get_author/{author}")
    @Operation (summary = "Get books by author", description = "Returns list of all books with matching author")
    public ResponseEntity<List<Book>> getBooksByAuthor(@Parameter (description="Author of the books",required = true) @PathVariable String author) {
        List<Book> books = bookService.findByAuthor(author);
        return ResponseEntity.ok(books);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    @Operation (summary = "Update book by id", description = "Updates information in database about book with matching id",security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<?> updateBook( @Parameter (description="ID of the book",required = true) @PathVariable Long id, @Valid @RequestBody Book book) {
        try {
            book.setId(id);
            Book updatedBook = bookService.updateBook(book);
            return ResponseEntity.ok(updatedBook);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    @Operation (summary = "Delete book by id", description = "Deletes information in database about book with matching id",security = @SecurityRequirement(name = "basicAuth"))
    public ResponseEntity<?> deleteBook(@Parameter (description="ID of the book",required = true) @PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/available_id/{id}")
    @Operation (summary = "Check if book is available by id", description = "Returns availabity of book by id")
    public ResponseEntity<?> isBookAvailable(@Parameter (description="ID of the book",required = true) @PathVariable Long id) {
        try {
            boolean isAvailable = bookService.isBookAvailable(id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("available", isAvailable);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/quantity/{id}")
    @Operation (summary = "Check quantity of the book by id", description = "Returns quantity of the book in database by id")
    public ResponseEntity<?> getAvailableQuantity(@Parameter (description="ID of the book",required = true) @PathVariable Long id) {
        try {
            int quantity = bookService.getAvailableQuantity(id);
            Map<String, Integer> response = new HashMap<>();
            response.put("availableQuantity", quantity);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}