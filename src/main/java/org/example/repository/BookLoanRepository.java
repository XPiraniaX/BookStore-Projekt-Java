package org.example.repository;

import org.example.entity.Book;
import org.example.entity.BookLoan;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {
    List<BookLoan> findByUser(User user);
    
    List<BookLoan> findByBook(Book book);
    
    List<BookLoan> findByUserAndReturned(User user, boolean returned);
    
    List<BookLoan> findByBookAndReturned(Book book, boolean returned);
    
    @Query("SELECT l FROM BookLoan l WHERE l.returned = false AND l.dueDate < :now")
    List<BookLoan> findOverdueLoans(@Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(l) FROM BookLoan l WHERE l.book = :book AND l.returned = false")
    long countActiveLoans(@Param("book") Book book);
}