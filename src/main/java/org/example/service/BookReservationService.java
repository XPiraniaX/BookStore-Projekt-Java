package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.Book;
import org.example.entity.BookReservation;
import org.example.entity.User;
import org.example.repository.BookRepository;
import org.example.repository.BookReservationRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookReservationService {

    private final BookReservationRepository bookReservationRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public BookReservation createReservation(Long userId, Long bookId, LocalDateTime expirationDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        
        if (book.getAvailableQuantity() <= 0) {
            throw new IllegalStateException("Book is not available for reservation");
        }
        
        if (hasActiveReservation(user, book)) {
            throw new IllegalStateException("User already has an active reservation for this book");
        }

        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        bookRepository.save(book);

        BookReservation reservation = BookReservation.builder()
                .user(user)
                .book(book)
                .reservationDate(LocalDateTime.now())
                .expirationDate(expirationDate)
                .active(true)
                .build();
        
        return bookReservationRepository.save(reservation);
    }


    @Transactional(readOnly = true)
    public Optional<BookReservation> findById(Long id) {
        return bookReservationRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<BookReservation> findByUser(User user) {
        return bookReservationRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public List<BookReservation> findByBook(Book book) {
        return bookReservationRepository.findByBook(book);
    }

    @Transactional(readOnly = true)
    public List<BookReservation> findActiveReservationsByUser(User user) {
        return bookReservationRepository.findByUserAndActive(user, true);
    }

    @Transactional(readOnly = true)
    public List<BookReservation> findActiveReservationsByBook(Book book) {
        return bookReservationRepository.findByBookAndActive(book, true);
    }

    @Transactional(readOnly = true)
    public List<BookReservation> findAllReservations() {
        return bookReservationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<BookReservation> findExpiredReservations() {
        return bookReservationRepository.findExpiredReservations(LocalDateTime.now());
    }

    public void cancelReservation(Long reservationId) {
        BookReservation reservation = bookReservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        
        if (!reservation.isActive()) {
            throw new IllegalStateException("Reservation is not active");
        }

        reservation.setActive(false);
        bookReservationRepository.save(reservation);

        Book book = reservation.getBook();
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);
        bookRepository.save(book);
    }

    public void processExpiredReservations() {
        List<BookReservation> expiredReservations = findExpiredReservations();
        
        for (BookReservation reservation : expiredReservations) {
            if (reservation.isActive()) {
                cancelReservation(reservation.getId());
            }
        }
    }

    @Transactional(readOnly = true)
    public boolean hasActiveReservation(User user, Book book) {
        return !bookReservationRepository.findByUserAndActive(user, true).isEmpty() &&
               !bookReservationRepository.findByBookAndActive(book, true).isEmpty();
    }

    @Transactional(readOnly = true)
    public long countActiveReservations(Book book) {
        return bookReservationRepository.countActiveReservationsByBook(book);
    }
}