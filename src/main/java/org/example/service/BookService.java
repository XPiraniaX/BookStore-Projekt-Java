package org.example.service;

import org.example.entity.Book;
import org.example.repository.BookLoanRepository;
import org.example.repository.BookRepository;
import org.example.repository.BookReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService  {

    private BookRepository bookRepository;
    private BookLoanRepository bookLoanRepository;
    private BookReservationRepository bookReservationRepository;

    public BookService(BookRepository bookRepository,
                           BookLoanRepository bookLoanRepository,
                           BookReservationRepository bookReservationRepository) {
        this.bookRepository = bookRepository;
        this.bookLoanRepository = bookLoanRepository;
        this.bookReservationRepository = bookReservationRepository;
    }

    public Book addBook(Book book) {
        if (book.getAvailableQuantity() == null) {
            book.setAvailableQuantity(book.getQuantity());
        }
        Book savedBook = bookRepository.save(book);
        return savedBook;
    }

    @Transactional(readOnly = true)
    public Optional<Book> findById(Long id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        return bookOptional;
    }

    @Transactional(readOnly = true)
    public List<Book> findAllBooks() {
        List<Book> allBooks = bookRepository.findAll();
        return allBooks;
    }

    @Transactional(readOnly = true)
    public List<Book> findByTitle(String title) {
        List<Book> books = bookRepository.findByTitle(title);
        return books;
    }

    @Transactional(readOnly = true)
    public List<Book> findByAuthor(String author) {
        List<Book> books = bookRepository.findByAuthor(author);
        return books;
    }

    @Transactional(readOnly = true)
    public List<Book> findAvailableBooks() {
        List<Book> availableBooks = bookRepository.findAllAvailableBooks();
        return availableBooks;
    }

    public Book updateBook(Book book) {
        boolean bookExists = bookRepository.existsById(book.getId());
        if (!bookExists) {
            throw new IllegalArgumentException("Book not found");
        }

        int availableQuantity = book.getAvailableQuantity();
        int totalQuantity = book.getQuantity();
        if (availableQuantity > totalQuantity) {
            book.setAvailableQuantity(totalQuantity);
        }

        Book updatedBook = bookRepository.save(book);
        return updatedBook;
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean isBookAvailable(Long bookId) {
        int availableQuantity = getAvailableQuantity(bookId);

        boolean isAvailable = availableQuantity > 0;
        return isAvailable;
    }

    @Transactional(readOnly = true)
    public int getAvailableQuantity(Long bookId) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);

        if (!bookOptional.isPresent()) {
            throw new IllegalArgumentException("Book not found");
        }

        Book book = bookOptional.get();

        return book.getAvailableQuantity();
    }
}