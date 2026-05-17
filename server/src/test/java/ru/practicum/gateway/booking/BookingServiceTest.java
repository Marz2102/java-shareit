package ru.practicum.gateway.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.server.booking.dto.BookingCreateDto;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.booking.service.BookingServiceImpl;
import ru.practicum.server.booking.storage.BookingRepository;
import ru.practicum.server.exception.exceptions.NotAvailableException;
import ru.practicum.server.exception.exceptions.ValidationException;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.service.ItemService;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void testCreateBooking() {
        User user = new User(1L, "name", "1@yandex.ru");
        Item item = new Item(2L, "itemName", "description", true, new User(), new ItemRequest());

        LocalDateTime start = LocalDateTime.now().withNano(0);
        LocalDateTime end = LocalDateTime.now().withNano(0).plusDays(1);
        BookingCreateDto bookingCreateDto = new BookingCreateDto(2L, start, end);

        when(userService.getUserById(eq(1L))).thenReturn(user);
        when(itemService.getItemById(eq(2L), eq(1L))).thenReturn(item);

        when(bookingRepository.save(any(Booking.class))).thenAnswer(answer -> {
            Booking booking = answer.getArgument(0);
            booking.setId(1L);
            return booking;
        });

        BookingDto bookingDto = bookingService.addBooking(bookingCreateDto, 1L);

        assertThat(bookingDto.getId()).isEqualTo(1L);
        assertThat(bookingDto.getStart()).isEqualTo(start);
        assertThat(bookingDto.getEnd()).isEqualTo(end);
        assertThat(bookingDto.getItem().getId()).isEqualTo(2L);
        assertThat(bookingDto.getItem().getName()).isEqualTo("itemName");
        assertThat(bookingDto.getItem().getDescription()).isEqualTo("description");
        assertThat(bookingDto.getBooker().getId()).isEqualTo(1L);
        assertThat(bookingDto.getBooker().getName()).isEqualTo("name");
        assertThat(bookingDto.getBooker().getEmail()).isEqualTo("1@yandex.ru");
    }

    @Test
    void testCreateBookingWhenBookerIsItemOwner() {
        User user = new User(1L, "name", "1@yandex.ru");
        Item item = new Item(2L, "itemName", "description", true, user, new ItemRequest());

        LocalDateTime start = LocalDateTime.now().withNano(0);
        LocalDateTime end = LocalDateTime.now().withNano(0).plusDays(1);
        BookingCreateDto bookingCreateDto = new BookingCreateDto(2L, start, end);

        when(userService.getUserById(eq(1L))).thenReturn(user);
        when(itemService.getItemById(eq(2L), eq(1L))).thenReturn(item);

        assertThatThrownBy(() -> bookingService.addBooking(bookingCreateDto, 1L))
                .isInstanceOf(NotAvailableException.class)
                .hasMessage("Владелец не может забронировать свой предмет");
    }

    @Test
    void testCreateBookingWhenItemIsNotAvailable() {
        User user = new User(1L, "name", "1@yandex.ru");
        Item item = new Item(2L, "itemName", "description", false, new User(), new ItemRequest());

        LocalDateTime start = LocalDateTime.now().withNano(0);
        LocalDateTime end = LocalDateTime.now().withNano(0).plusDays(1);
        BookingCreateDto bookingCreateDto = new BookingCreateDto(2L, start, end);

        when(userService.getUserById(eq(1L))).thenReturn(user);
        when(itemService.getItemById(eq(2L), eq(1L))).thenReturn(item);

        assertThatThrownBy(() -> bookingService.addBooking(bookingCreateDto, 1L))
                .isInstanceOf(NotAvailableException.class)
                .hasMessage("Предмет с id - 2 недоступен для бронирования");
    }

    @Test
    void testUpdateBookingStatus() {
        LocalDateTime start = LocalDateTime.now().withNano(0);
        LocalDateTime end = LocalDateTime.now().withNano(0).plusDays(1);

        User user = new User(1L, "name", "1@yandex.ru");
        Item item = new Item(2L, "itemName", "description", false, user, new ItemRequest());
        Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);

        when(bookingRepository.findById(any(Long.class))).thenReturn(Optional.of(booking));
        when(userService.existsById(eq(1L))).thenReturn(true);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(answer -> {
            Booking updatedBooking = answer.getArgument(0);
            updatedBooking.setStatus(BookingStatus.APPROVED);
            return updatedBooking;
        });

        BookingDto bookingDto = bookingService.updateBookingStatus(2L, true, 1L);

        assertThat(bookingDto.getId()).isEqualTo(1L);
        assertThat(bookingDto.getStart()).isEqualTo(start);
        assertThat(bookingDto.getEnd()).isEqualTo(end);
        assertThat(bookingDto.getItem().getId()).isEqualTo(2L);
        assertThat(bookingDto.getItem().getName()).isEqualTo("itemName");
        assertThat(bookingDto.getItem().getDescription()).isEqualTo("description");
        assertThat(bookingDto.getBooker().getId()).isEqualTo(1L);
        assertThat(bookingDto.getBooker().getName()).isEqualTo("name");
        assertThat(bookingDto.getBooker().getEmail()).isEqualTo("1@yandex.ru");
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void testUpdateBookingStatusWhenItsNotWaiting() {
        User user = new User(1L, "name", "1@yandex.ru");
        Item item = new Item(2L, "itemName", "description", false, user, new ItemRequest());
        Booking booking = new Booking(1L, null, null, item, user, BookingStatus.APPROVED);

        when(bookingRepository.findById(any(Long.class))).thenReturn(Optional.of(booking));
        when(userService.existsById(eq(1L))).thenReturn(true);

        assertThatThrownBy(() -> bookingService.updateBookingStatus(2L, false, 1L))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Для подтверждения бронирования оно должно находиться в статусе ожидания");
    }

    @Test
    void testGetBookingsForUser() {
        LocalDateTime start = LocalDateTime.now().withNano(0);
        LocalDateTime end = LocalDateTime.now().withNano(0).plusDays(1);

        User user = new User(1L, "name", "1@yandex.ru");
        Item item = new Item(2L, "itemName", "description", false, user, new ItemRequest());
        Booking booking = new Booking(3L, start, end, item, user, BookingStatus.WAITING);

        when(userService.existsById(eq(1L))).thenReturn(true);
        when(bookingRepository.findAllCurrentBookingsByBookerIdOrderByStartDesc(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsForUser(1L, "CURRENT");

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().getId()).isEqualTo(3L);
        assertThat(result.getFirst().getStart()).isEqualTo(start);
        assertThat(result.getFirst().getEnd()).isEqualTo(end);
        assertThat(result.getFirst().getItem().getId()).isEqualTo(2L);
        assertThat(result.getFirst().getItem().getName()).isEqualTo("itemName");
        assertThat(result.getFirst().getItem().getDescription()).isEqualTo("description");
        assertThat(result.getFirst().getBooker().getId()).isEqualTo(1L);
        assertThat(result.getFirst().getBooker().getName()).isEqualTo("name");
        assertThat(result.getFirst().getBooker().getEmail()).isEqualTo("1@yandex.ru");
        assertThat(result.getFirst().getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void testGetBookingsForUserWhenStateParamIncorrect() {
        when(userService.existsById(any(Long.class))).thenReturn(true);

        assertThatThrownBy(() -> bookingService.getBookingsForUser(1L, "SOME_STATE"))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Укажите корректный параметр state. " +
                        "Возможные значения: CURRENT, PAST, FUTURE, WAITING, REJECTED, ALL");
    }

    @Test
    void testGetBookingsForItemsByUser() {
        LocalDateTime start = LocalDateTime.now().withNano(0);
        LocalDateTime end = LocalDateTime.now().withNano(0).plusDays(1);

        User user = new User(1L, "name", "1@yandex.ru");
        Item item = new Item(2L, "itemName", "description", false, user, new ItemRequest());
        Booking booking = new Booking(3L, start, end, item, user, BookingStatus.REJECTED);

        when(userService.existsById(eq(1L))).thenReturn(true);
        when(bookingRepository.findAllFutureItemBookingsByOwnerIdOrderByStartDesc(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsForItemsByUser(1L, "future");

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().getId()).isEqualTo(3L);
        assertThat(result.getFirst().getStart()).isEqualTo(start);
        assertThat(result.getFirst().getEnd()).isEqualTo(end);
        assertThat(result.getFirst().getItem().getId()).isEqualTo(2L);
        assertThat(result.getFirst().getItem().getName()).isEqualTo("itemName");
        assertThat(result.getFirst().getItem().getDescription()).isEqualTo("description");
        assertThat(result.getFirst().getBooker().getId()).isEqualTo(1L);
        assertThat(result.getFirst().getBooker().getName()).isEqualTo("name");
        assertThat(result.getFirst().getBooker().getEmail()).isEqualTo("1@yandex.ru");
        assertThat(result.getFirst().getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void testGetBookingsForItemsByUserWhenStateParamIncorrect() {
        when(userService.existsById(any(Long.class))).thenReturn(true);

        assertThatThrownBy(() -> bookingService.getBookingsForItemsByUser(1L, "SOME_STATE"))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Укажите корректный параметр state. " +
                        "Возможные значения: CURRENT, PAST, FUTURE, WAITING, REJECTED, ALL");
    }
}
