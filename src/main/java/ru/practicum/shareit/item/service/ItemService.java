package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item getItemById(Long id, Long userId);

    ItemDto addItem(ItemCreateDto itemCreateDto, Long userId);

    ItemDto updateItem(Long id, ItemCreateDto itemCreateDto, Long userId);

    List<ItemDto> searchItems(String query, Long userId);

    CommentDto addComment(CommentCreateDto commentCreateDto, Long userId, Long itemId);

    ItemCommentsDto getCommentsByItemId(Long itemId, Long userId);

    List<ItemCommentsDto> getCommentsForUserItems(Long userId);
}
