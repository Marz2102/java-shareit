package ru.practicum.shareit.booking.service;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.rmi.ServerException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    BookingServiceImpl(final BookingRepository bookingRepository, final UserService userService, final ItemService itemService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public BookingDto getBookingDtoById(Long id, Long userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирования с id - " + id + " не найдено"));

        User owner = booking.getItem().getOwner();

        if (!userService.existsById(userId)) {
            throw new ServerRequestException("Пользователя, отправившего запрос, не существует");
        }

        if (!Objects.equals(userId, owner.getId()) && !Objects.equals(userId, booking.getBooker().getId())) {
            throw new DataAccessException("Посмотреть статус бронирования может только владелец предмета или автор брони");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирования с id - " + id + " не найдено"));
    }

    @Override
    public List<BookingDto> getBookingsForUser(Long userId, BookingState state) {
        if (!userService.existsById(userId)) {
            throw new ServerRequestException("Пользователя, отправившего запрос, не существует");
        }

        List<Booking> bookings = switch (state) {
            case CURRENT -> bookingRepository.findAllCurrentBookingsByBookerIdOrderByStartDesc(userId, LocalDateTime.now());
            case PAST -> bookingRepository.findAllPastBookingsByBookerIdOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllFutureBookingsByBookerIdOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getBookingsForItemsByUser(Long userId, BookingState state) {
        if (!userService.existsById(userId)) {
            throw new ServerRequestException("Пользователя, отправившего запрос, не существует");
        }

        List<Booking> bookings = switch (state) {
            case CURRENT -> bookingRepository.findAllCurrentItemBookingsByOwnerIdOrderByStartDesc(userId, LocalDateTime.now());
            case PAST -> bookingRepository.findAllPastItemBookingsByOwnerIdOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllFutureItemBookingsByOwnerIdOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public BookingDto addBooking(BookingCreateDto bookingCreateDto, Long userId) {
        User user = userService.getUserById(userId);
        Item item = itemService.getItemById(bookingCreateDto.getItemId(), userId);

        if (bookingCreateDto.getStart().isEqual(bookingCreateDto.getEnd())) {
            throw new ValidationException("Укажите разные даты начала и окончания бронирования");
        }

        if (!item.isAvailable()) {
            throw new NotAvailableException("Предмет с id - " + item.getId() + " недоступен для бронирования");
        }

        Booking booking = BookingMapper.toBooking(bookingCreateDto);
        booking.setItem(item);
        booking.setBooker(user);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto updateBookingStatus(Long id, boolean isApproved, Long userId) {
        Booking booking = getBookingById(id);
        User owner = booking.getItem().getOwner();

        if (!userService.existsById(userId)) {
            throw new ServerRequestException("Пользователя, отправившего запрос, не существует");
        }

        if (!Objects.equals(owner.getId(), userId)) {
            throw new DataAccessException("Бронирование может подтвердить только владелец предмета с id - " + owner.getId());
        }

        BookingMapper.updateBookingStatus(booking, isApproved);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }
}
