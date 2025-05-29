package org.example.service;

import org.example.entity.Book;
import org.example.entity.BookLoan;
import org.example.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
public abstract class AbstractBookLoanService {

    public abstract BookLoan createLoan(Long userId, Long bookId, LocalDateTime dueDate);

    @Transactional(readOnly = true)
    public abstract Optional<BookLoan> findById(Long id);

    @Transactional(readOnly = true)
    public abstract List<BookLoan> findByUser(User user);

    @Transactional(readOnly = true)
    public abstract List<BookLoan> findByBook(Book book);

    @Transactional(readOnly = true)
    public abstract List<BookLoan> findActiveLoans();

    @Transactional(readOnly = true)
    public abstract List<BookLoan> findActiveLoansForUser(User user);

    @Transactional(readOnly = true)
    public abstract List<BookLoan> findActiveLoansForBook(Book book);

    @Transactional(readOnly = true)
    public abstract List<BookLoan> findAllLoans();

    @Transactional(readOnly = true)
    public abstract List<BookLoan> findOverdueLoans();

    public abstract BookLoan returnBook(Long loanId);

    @Transactional(readOnly = true)
    public abstract boolean hasActiveLoan(User user, Book book);

    @Transactional(readOnly = true)
    public abstract long countActiveLoans(Book book);
}