package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getItemById(Long id);

    List<ItemDto> getItems(Long userId);

    ItemDto addItem(ItemCreateDto itemCreateDto, Long UserId);

    ItemDto updateItem(Long id, ItemCreateDto itemCreateDto, Long UserId);

    List<ItemDto> searchItems(String query);
}
