package ru.practicum.server.item.mapper;


import ru.practicum.server.item.dto.ItemCommentsDto;
import ru.practicum.server.item.dto.ItemCreateDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());

        return itemDto;
    }

    public static ItemCommentsDto toItemCommentsDto(Item item) {
        ItemCommentsDto itemCommentsDto = new ItemCommentsDto();

        itemCommentsDto.setId(item.getId());
        itemCommentsDto.setName(item.getName());
        itemCommentsDto.setDescription(item.getDescription());
        itemCommentsDto.setAvailable(item.isAvailable());

        return itemCommentsDto;
    }

    public static Item toItem(ItemCreateDto itemCreateDto) {
        Item item = new Item();

        item.setName(itemCreateDto.getName());
        item.setDescription(itemCreateDto.getDescription());
        item.setAvailable(itemCreateDto.getAvailable());

        return item;
    }

    public static Item updateItem(Item item, ItemCreateDto itemCreateDto) {
        if (itemCreateDto.getName() != null && !itemCreateDto.getName().isEmpty()) {
            item.setName(itemCreateDto.getName());
        }

        if (itemCreateDto.getDescription() != null && !itemCreateDto.getDescription().isEmpty()) {
            item.setDescription(itemCreateDto.getDescription());
        }

        if (itemCreateDto.getAvailable() != null) {
            item.setAvailable(itemCreateDto.getAvailable());
        }

        return item;
    }
}
