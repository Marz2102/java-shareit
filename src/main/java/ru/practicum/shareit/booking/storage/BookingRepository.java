package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    @Query("SELECT b FROM Booking as b " +
    "WHERE b.booker.id = ?1 AND ?2 BETWEEN b.start AND b.end " +
    "ORDER BY b.start DESC")
    List<Booking> findAllCurrentBookingsByBookerIdOrderByStartDesc(Long userId, LocalDateTime date);

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.booker.id = ?1 AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllPastBookingsByBookerIdOrderByStartDesc(Long userId, LocalDateTime date);

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.booker.id = ?1 AND ?2 < b.start " +
            "ORDER BY b.start DESC")
    List<Booking> findAllFutureBookingsByBookerIdOrderByStartDesc(Long userId, LocalDateTime date);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId);

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.item.owner.id = ?1 AND ?2 BETWEEN b.start AND b.end " +
            "ORDER BY b.start DESC")
    List<Booking> findAllCurrentItemBookingsByOwnerIdOrderByStartDesc(Long userId, LocalDateTime date);

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.item.owner.id = ?1 AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllPastItemBookingsByOwnerIdOrderByStartDesc(Long userId, LocalDateTime date);

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.item.owner.id = ?1 AND ?2 < b.start " +
            "ORDER BY b.start DESC")
    List<Booking> findAllFutureItemBookingsByOwnerIdOrderByStartDesc(Long userId, LocalDateTime date);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status);
}
