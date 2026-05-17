package ru.practicum.server.booking;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.booking.dto.BookingCreateDto;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(final BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @RequestBody BookingCreateDto booking,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingService.addBooking(booking, userId));
    }

    @PatchMapping(path = "/{bookingId}")
    public ResponseEntity<BookingDto> updateBooking(
            @PathVariable("bookingId") Long id,
            @RequestParam(name = "approved") boolean isApproved,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, isApproved, userId));
    }

    @GetMapping(path = "/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(
            @PathVariable("bookingId") Long id,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(bookingService.getBookingDtoById(id, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getBookingsForUser(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsForUser(userId, state, from, size));
    }

    @GetMapping(path = "/owner")
    public ResponseEntity<List<BookingDto>> getBookingsForItemsByUser(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsForItemsByUser(userId, state, from, size));
    }
}
