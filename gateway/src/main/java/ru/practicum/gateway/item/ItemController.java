package ru.practicum.gateway.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.item.dto.CommentCreateDto;
import ru.practicum.gateway.item.dto.ItemCreateDto;

@RestController
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemClient itemClient;

    public ItemController(final ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @GetMapping
    public ResponseEntity<Object> getCommentsForUserItems(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getCommentsForUserItems(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(
            @Validated(ItemCreateDto.OnCreate.class) @RequestBody ItemCreateDto item,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.addItem(item, userId);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Object> updateItem(
            @Validated(ItemCreateDto.Default.class) @RequestBody ItemCreateDto item,
            @Positive @PathVariable Long id,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.updateItem(id, item, userId);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<Object> searchItems(
            @RequestParam(name = "text", defaultValue = "") String text,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.searchItems(text, userId);
    }

    @PostMapping(path = "/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @Valid @RequestBody CommentCreateDto commentCreateDto,
            @Positive @PathVariable Long itemId,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.addComment(commentCreateDto, userId, itemId);
    }

    @GetMapping(path = "/{itemId}")
    public ResponseEntity<Object> getCommentsByItemId(
            @Positive @PathVariable Long itemId,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getCommentsByItemId(itemId, userId);
    }
}