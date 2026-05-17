package ru.practicum.gateway.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.booking.dto.BookingCreateDto;
import ru.practicum.gateway.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> addBooking(BookingCreateDto booking, Long userId) {
        return post("", userId, booking);
    }

    public ResponseEntity<Object> updateBookingStatus(Long id, boolean isApproved, Long userId) {
        return patch("/{id}?approved={approved}", userId, Map.of("approved", Boolean.toString(isApproved), "id", id), null);
    }

    public ResponseEntity<Object> getBookingDtoById(Long id, Long userId) {
        return get("/{id}", userId, Map.of("id", id));
    }

    public ResponseEntity<Object> getBookingsForUser(Long userId, String stateParam, Integer from, Integer size) {
        Map<String, Object> params = Map.of(
                "state", stateParam,
                "from", from,
                "size", size);
        return get("?state={state}&from={from}&size={size}", userId, params);
    }

    public ResponseEntity<Object> getBookingsForItemsByUser(Long userId, String stateParam, Integer from, Integer size) {
        Map<String, Object> params = Map.of(
                "state", stateParam,
                "from", from,
                "size", size);
        return get("/owner?state={state}&from={from}&size={size}", userId, params);
    }
}
