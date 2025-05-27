package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.Book;
import org.example.entity.BookLoan;
import org.example.entity.BookReservation;
import org.example.entity.User;
import org.example.repository.BookRepository;
import org.example.repository.BookLoanRepository;
import org.example.repository.BookReservationRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookLoanService {

    private final BookLoanRepository bookLoanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookReservationRepository bookReservationRepository;

    public BookLoan createLoan(Long userId, Long bookId, LocalDateTime dueDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        
        if (book.getAvailableQuantity() <= 0) {
            throw new IllegalStateException("Book is not available for loan");
        }
        
        if (hasActiveLoan(user, book)) {
            throw new IllegalStateException("User already has an active loan for this book");
        }

        List<BookReservation> activeReservations = bookReservationRepository.findByUserAndActive(user, true);
        boolean hasReservation = activeReservations.stream()
                .anyMatch(reservation -> reservation.getBook().getId().equals(bookId));
        
        if (!hasReservation) {
            long activeReservationsCount = bookReservationRepository.countActiveReservationsByBook(book);
            if (activeReservationsCount > 0) {
                throw new IllegalStateException("Book is reserved by other users");
            }
        } else {
            BookReservation reservation = activeReservations.stream()
                    .filter(r -> r.getBook().getId().equals(bookId))
                    .findFirst()
                    .orElseThrow();
            
            reservation.setActive(false);
            bookReservationRepository.save(reservation);
        }

        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        bookRepository.save(book);

        BookLoan loan = BookLoan.builder()
                .user(user)
                .book(book)
                .loanDate(LocalDateTime.now())
                .dueDate(dueDate)
                .returned(false)
                .build();
        
        return bookLoanRepository.save(loan);
    }


    @Transactional(readOnly = true)
    public Optional<BookLoan> findById(Long id) {
        return bookLoanRepository.findById(id);
    }


    @Transactional(readOnly = true)
    public List<BookLoan> findByUser(User user) {
        return bookLoanRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public List<BookLoan> findByBook(Book book) {
        return bookLoanRepository.findByBook(book);
    }

    @Transactional(readOnly = true)
    public List<BookLoan> findActiveLoans() {
        return bookLoanRepository.findAll().stream()
                .filter(loan -> !loan.isReturned())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookLoan> findActiveLoansForUser(User user) {
        return bookLoanRepository.findByUserAndReturned(user, false);
    }

    @Transactional(readOnly = true)
    public List<BookLoan> findActiveLoansForBook(Book book) {
        return bookLoanRepository.findByBookAndReturned(book, false);
    }

    @Transactional(readOnly = true)
    public List<BookLoan> findAllLoans() {
        return bookLoanRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<BookLoan> findOverdueLoans() {
        return bookLoanRepository.findOverdueLoans(LocalDateTime.now());
    }

    public BookLoan returnBook(Long loanId) {
        BookLoan loan = bookLoanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));
        
        if (loan.isReturned()) {
            throw new IllegalStateException("Book has already been returned");
        }

        loan.setReturned(true);
        loan.setReturnDate(LocalDateTime.now());

        Book book = loan.getBook();
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);
        bookRepository.save(book);
        
        return bookLoanRepository.save(loan);
    }

    @Transactional(readOnly = true)
    public boolean hasActiveLoan(User user, Book book) {
        return !bookLoanRepository.findByUserAndReturned(user, false).isEmpty() &&
               !bookLoanRepository.findByBookAndReturned(book, false).isEmpty();
    }

    @Transactional(readOnly = true)
    public long countActiveLoans(Book book) {
        return bookLoanRepository.countActiveLoans(book);
    }
}