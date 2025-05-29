package org.example.service;

import org.example.entity.Book;
import org.example.repository.BookLoanRepository;
import org.example.repository.BookRepository;
import org.example.repository.BookReservationRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public abstract class AbstractBookService {

    protected BookRepository bookRepository;
    protected BookLoanRepository bookLoanRepository;
    protected BookReservationRepository bookReservationRepository;

    public AbstractBookService(BookRepository bookRepository,
                       BookLoanRepository bookLoanRepository,
                       BookReservationRepository bookReservationRepository) {
        this.bookRepository = bookRepository;
        this.bookLoanRepository = bookLoanRepository;
        this.bookReservationRepository = bookReservationRepository;
    }

    public abstract Book addBook(Book book);

    @Transactional(readOnly = true)
    public abstract Optional<Book> findById(Long id);

    @Transactional(readOnly = true)
    public abstract List<Book> findAllBooks();

    @Transactional(readOnly = true)
    public abstract List<Book> findByTitle(String title);

    @Transactional(readOnly = true)
    public abstract List<Book> findByAuthor(String author);

    @Transactional(readOnly = true)
    public abstract List<Book> findAvailableBooks();

    public abstract Book updateBook(Book book);

    public abstract void deleteBook(Long id);

    @Transactional(readOnly = true)
    public abstract boolean isBookAvailable(Long bookId);

    @Transactional(readOnly = true)
    public abstract int getAvailableQuantity(Long bookId);
}