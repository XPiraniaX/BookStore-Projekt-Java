package org.example.repository;

import org.example.entity.Book;
import org.example.entity.BookReservation;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookReservationRepository extends JpaRepository<BookReservation, Long> {
    List<BookReservation> findByUser(User user);
    
    List<BookReservation> findByBook(Book book);
    
    List<BookReservation> findByUserAndActive(User user, boolean active);
    
    List<BookReservation> findByBookAndActive(Book book, boolean active);
    
    @Query("SELECT r FROM BookReservation r WHERE r.active = true AND r.expirationDate < :now")
    List<BookReservation> findExpiredReservations(@Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(r) FROM BookReservation r WHERE r.book = :book AND r.active = true")
    long countActiveReservationsByBook(@Param("book") Book book);
}