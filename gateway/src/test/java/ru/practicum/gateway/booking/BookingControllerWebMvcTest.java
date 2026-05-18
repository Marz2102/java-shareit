package ru.practicum.gateway.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.gateway.booking.dto.BookingCreateDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateBooking() throws Exception {
        LocalDateTime startTime = LocalDateTime.now().plusYears(1);
        LocalDateTime endTime = LocalDateTime.now().plusYears(2);

        BookingCreateDto bookingCreateDto = new BookingCreateDto(1L, startTime, endTime);
        ResponseEntity<Object> response = ResponseEntity.status(201).body((Map.of("itemId", 1L, "start", startTime.toString(),
                "end", endTime.toString())));

        when(bookingClient.addBooking(any(BookingCreateDto.class), eq(1L))).thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemId").value(1L))
                .andExpect(jsonPath("$.start").value(startTime.toString()))
                .andExpect(jsonPath("$.end").value(endTime.toString()));
    }

    @Test
    void testCreateBookingWithStartInPastYear() throws Exception {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(1L, LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(2));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateBooking() throws Exception {
        LocalDateTime startTime = LocalDateTime.now().plusYears(1);
        LocalDateTime endTime = LocalDateTime.now().plusYears(2);

        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("itemId", 1L, "start", startTime.toString(),
                "end", endTime.toString()));

        when(bookingClient.updateBookingStatus(eq(1L), eq(true), eq(1L))).thenReturn(response);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(1L))
                .andExpect(jsonPath("$.start").value(startTime.toString()))
                .andExpect(jsonPath("$.end").value(endTime.toString()));
    }

    @Test
    void testGetBooking() throws Exception {
        LocalDateTime startTime = LocalDateTime.now().plusYears(1);
        LocalDateTime endTime = LocalDateTime.now().plusYears(2);

        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("itemId", 1L, "start", startTime.toString(),
                "end", endTime.toString()));

        when(bookingClient.getBookingDtoById(eq(1L), eq(1L))).thenReturn(response);

        mockMvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(1L))
                .andExpect(jsonPath("$.start").value(startTime.toString()))
                .andExpect(jsonPath("$.end").value(endTime.toString()));
    }

    @Test
    void testGetBookingWhenBookingNotFound() throws Exception {
        when(bookingClient.getBookingDtoById(eq(1L), eq(1L))).thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetBookingsForUser() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ok(List.of());

        when(bookingClient.getBookingsForUser(eq(1L), any(String.class), any(Integer.class), any(Integer.class))).thenReturn(response);

        mockMvc.perform(get("/bookings?state=ALL&from=1&size=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBookingsForUserWithoutHeader() throws Exception {
        mockMvc.perform(get("/bookings?"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBookingsForItemsByUser() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ok(List.of());

        when(bookingClient.getBookingsForItemsByUser(eq(1L), any(String.class), any(Integer.class), any(Integer.class))).thenReturn(response);

        mockMvc.perform(get("/bookings/owner?state=ALL&from=1&size=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBookingsForItemsByUserWithoutHeader() throws Exception {
        mockMvc.perform(get("/bookings/owner?state=FUTURE&from=1&size=1"))
                .andExpect(status().isBadRequest());
    }
}
