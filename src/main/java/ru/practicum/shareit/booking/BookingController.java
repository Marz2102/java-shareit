package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

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
            @Valid @RequestBody BookingCreateDto booking,
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
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsForUser(userId, BookingState.valueOf(state)));
    }

    @GetMapping(path = "/owner")
    public ResponseEntity<List<BookingDto>> getBookingsForItemsByUser(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsForItemsByUser(userId, BookingState.valueOf(state)));
    }
}
