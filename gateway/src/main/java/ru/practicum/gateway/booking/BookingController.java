package ru.practicum.gateway.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.booking.dto.BookingCreateDto;

@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    public BookingController(final BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @Valid @RequestBody BookingCreateDto booking,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.addBooking(booking, userId);
    }

    @PatchMapping(path = "/{bookingId}")
    public ResponseEntity<Object> updateBooking(
            @Positive @PathVariable("bookingId") Long id,
            @RequestParam(name = "approved") boolean isApproved,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.updateBookingStatus(id, isApproved, userId);
    }

    @GetMapping(path = "/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @Positive @PathVariable("bookingId") Long id,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.getBookingDtoById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsForUser(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.getBookingsForUser(userId, state, from, size);
    }

    @GetMapping(path = "/owner")
    public ResponseEntity<Object> getBookingsForItemsByUser(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.getBookingsForItemsByUser(userId, state, from, size);
    }
}
