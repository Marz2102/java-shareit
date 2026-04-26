package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    public ItemServiceImpl(final ItemStorage itemStorage, final UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public ItemDto getItemById(Long id) {
        return ItemMapper.toItemDto(itemStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Предмет с id - " + id + " не найден")));
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        return itemStorage
                .getItems(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto addItem(ItemCreateDto itemCreateDto, Long userId) {
        Item item = ItemMapper.toItem(itemCreateDto);
        User user = userService.getUserById(userId);

        item.setOwner(user);

        return ItemMapper.toItemDto(itemStorage.addItem(item));
    }

    @Override
    public ItemDto updateItem(Long id, ItemCreateDto itemCreateDto, Long userId) {
        Item updatedItem = itemStorage.findById(id)
                .map(item -> ItemMapper.updateItem(item, itemCreateDto))
                .orElseThrow(() -> new NotFoundException("Предмет с id - " + id + " не найден"));

        if (!updatedItem.getOwner().getId().equals(userId)) {
            throw new DataAccessException("У пользователя с id - " + userId + " нет возможности редактировать предмет с id - " + updatedItem.getId());
        }

        return ItemMapper.toItemDto(itemStorage.updateItem(updatedItem));
    }

    @Override
    public List<ItemDto> searchItems(String query) {
        if (query.isEmpty()) {
            return List.of();
        }

        return itemStorage
                .searchItems(query.toLowerCase())
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
