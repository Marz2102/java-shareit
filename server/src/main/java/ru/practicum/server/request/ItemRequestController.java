package ru.practicum.server.request;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.request.dto.CreateRequestDto;
import ru.practicum.server.request.dto.FullItemRequestDto;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    public ItemRequestController(final ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(
            @RequestBody CreateRequestDto itemRequestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(itemRequestService.createRequest(itemRequestDto, userId));
    }

    @GetMapping
    public ResponseEntity<List<FullItemRequestDto>> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemRequestService.getRequests(userId));
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<ItemRequestDto>> getOtherRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemRequestService.getOtherRequests(userId));
    }

    @GetMapping(path = "/{requestId}")
    public ResponseEntity<FullItemRequestDto> getRequestDtoById(
            @PathVariable("requestId") Long requestId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemRequestService.getRequestDtoById(requestId, userId));
    }
}
