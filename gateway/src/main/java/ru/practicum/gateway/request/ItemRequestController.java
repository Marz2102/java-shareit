package ru.practicum.gateway.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.request.dto.CreateRequestDto;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    public ItemRequestController(final ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(
            @Valid @RequestBody CreateRequestDto itemRequestDto,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Object> getOtherRequests(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getOtherRequests(userId);
    }

    @GetMapping(path = "/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @Positive @PathVariable("requestId") Long requestId,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getRequestById(requestId, userId);
    }
}
