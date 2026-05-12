package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    BookingDto getBookingDtoById(Long id, Long userId);

    Booking getBookingById(Long id);

    List<BookingDto> getBookingsForUser(Long userId, String state);

    List<BookingDto> getBookingsForItemsByUser(Long userId, String state);

    BookingDto addBooking(BookingCreateDto bookingCreateDto, Long userId);

    BookingDto updateBookingStatus(Long id, boolean isApproved, Long userId);
}
