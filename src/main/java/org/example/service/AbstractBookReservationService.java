package org.example.service;

import org.example.entity.Book;
import org.example.entity.BookReservation;
import org.example.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
public abstract class AbstractBookReservationService {

    public abstract BookReservation createReservation(Long userId, Long bookId, LocalDateTime expirationDate);

    @Transactional(readOnly = true)
    public abstract Optional<BookReservation> findById(Long id);

    @Transactional(readOnly = true)
    public abstract List<BookReservation> findByUser(User user);

    @Transactional(readOnly = true)
    public abstract List<BookReservation> findByBook(Book book);

    @Transactional(readOnly = true)
    public abstract List<BookReservation> findActiveReservationsByUser(User user);

    @Transactional(readOnly = true)
    public abstract List<BookReservation> findActiveReservationsByBook(Book book);

    @Transactional(readOnly = true)
    public abstract List<BookReservation> findAllReservations();

    @Transactional(readOnly = true)
    public abstract List<BookReservation> findExpiredReservations();

    public abstract void cancelReservation(Long reservationId);

    public abstract void processExpiredReservations();

    @Transactional(readOnly = true)
    public abstract boolean hasActiveReservation(User user, Book book);

    @Transactional(readOnly = true)
    public abstract long countActiveReservations(Book book);
}