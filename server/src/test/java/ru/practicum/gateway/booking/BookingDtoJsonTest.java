package ru.practicum.gateway.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.server.ShareItServer;
import ru.practicum.server.booking.dto.BookingCreateDto;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoJsonTest {
    private final JacksonTester<BookingDto> jsonBookingDto;
    private final JacksonTester<BookingCreateDto> jsonBookingCreateDto;

    @Test
    void testSerializeBookingDto() throws Exception {
        LocalDateTime start = LocalDateTime.now().withNano(0);
        LocalDateTime end = LocalDateTime.now().plusDays(1).withNano(0);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setItem(new ItemDto(1L, "item", "description",true));
        bookingDto.setBooker(new UserDto(1L, "name", "email"));
        bookingDto.setStatus(BookingStatus.APPROVED);

        JsonContent<BookingDto> jsonContent = jsonBookingDto.write(bookingDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(jsonContent).extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
        assertThat(jsonContent).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.item.name").isEqualTo("item");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.booker.email").isEqualTo("email");
        assertThat(jsonContent).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }

    @Test
    void testDeserializeBookingCreateDto() throws Exception {
        LocalDateTime start = LocalDateTime.now().withNano(0);
        LocalDateTime end = LocalDateTime.now().plusDays(1).withNano(0);

        String json = String.format("{\"itemId\":1,\"start\":\"%s\",\"end\":\"%s\"}", start, end);

        BookingCreateDto bookingCreateDto = jsonBookingCreateDto.parse(json).getObject();

        assertThat(bookingCreateDto.getItemId()).isEqualTo(1);
        assertThat(bookingCreateDto.getStart()).isEqualTo(start.toString());
        assertThat(bookingCreateDto.getEnd()).isEqualTo(end.toString());
    }
}
