package ru.practicum.gateway.booking;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.booking.dto.BookingCreateDto;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    public BookingController(final BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @Valid @RequestBody BookingCreateDto booking,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.addBooking(booking, userId);
    }

    @PatchMapping(path = "/{bookingId}")
    public ResponseEntity<Object> updateBooking(
            @PathVariable("bookingId") Long id,
            @RequestParam(name = "approved") boolean isApproved,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.updateBookingStatus(id, isApproved, userId);
    }

    @GetMapping(path = "/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @PathVariable("bookingId") Long id,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.getBookingDtoById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsForUser(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.getBookingsForUser(userId, state);
    }

    @GetMapping(path = "/owner")
    public ResponseEntity<Object> getBookingsForItemsByUser(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.getBookingsForItemsByUser(userId, state);
    }
}
