package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(final ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ItemDto> getItemById(
            @PathVariable Long id,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.getItemDtoById(id, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.getItems(userId));
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(
            @Validated(ItemCreateDto.OnCreate.class) @RequestBody ItemCreateDto item,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(itemService.addItem(item, userId));
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<ItemDto> updateItem(
            @Validated(ItemCreateDto.Default.class) @RequestBody ItemCreateDto item,
            @PathVariable Long id,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.updateItem(id, item, userId));
    }

    @GetMapping(path = "/search")
    public ResponseEntity<List<ItemDto>> searchItems(
            @RequestParam(name = "text", defaultValue = "") String text,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.searchItems(text, userId));
    }

    @PostMapping(path = "/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(
            @Valid @RequestBody CommentCreateDto commentCreateDto,
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(itemService.addComment(commentCreateDto, userId, itemId));
    }

    @GetMapping(path = "/{itemId}")
    public ResponseEntity<ItemCommentsDto> getCommentsByItemId(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.getCommentsByItemId(itemId, userId));
    }
}
