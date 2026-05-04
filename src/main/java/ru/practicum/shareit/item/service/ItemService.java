package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto getItemDtoById(Long id, Long userId);

    Item getItemById(Long id, Long userId);

    List<ItemDto> getItems(Long userId);

    ItemDto addItem(ItemCreateDto itemCreateDto, Long userId);

    ItemDto updateItem(Long id, ItemCreateDto itemCreateDto, Long userId);

    List<ItemDto> searchItems(String query, Long userId);
}
