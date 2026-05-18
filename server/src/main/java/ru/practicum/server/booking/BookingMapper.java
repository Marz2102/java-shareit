package ru.practicum.server.booking;

import lombok.NoArgsConstructor;
import ru.practicum.server.booking.dto.BookingCreateDto;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.item.mapper.ItemMapper;
import ru.practicum.server.user.UserMapper;

@NoArgsConstructor
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setItem(ItemMapper.toItemDto(booking.getItem()));
        bookingDto.setBooker(UserMapper.toUserDto(booking.getBooker()));

        return bookingDto;
    }

    public static Booking toBooking(BookingCreateDto bookingCreateDto) {
        Booking booking = new Booking();

        booking.setStart(bookingCreateDto.getStart());
        booking.setEnd(bookingCreateDto.getEnd());
        booking.setStatus(BookingStatus.WAITING);

        return booking;
    }

    public static void updateBookingStatus(Booking booking, boolean isApproved) {
        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
    }
}
