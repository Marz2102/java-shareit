package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemServiceImpl(final ItemRepository itemRepository, final UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public ItemDto getItemById(Long id, Long userId) {
        return ItemMapper.toItemDto(itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Предмет с id - " + id + " не найден")));
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        return itemRepository
                .findByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto addItem(ItemCreateDto itemCreateDto, Long userId) {
        Item item = ItemMapper.toItem(itemCreateDto);
        User user = userService.getUserById(userId);

        item.setOwner(user);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long id, ItemCreateDto itemCreateDto, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Предмет с id - " + id + " не найден"));

        if (item.getOwner() != null && !item.getOwner().getId().equals(userId)) {
            throw new DataAccessException("У пользователя с id - " + userId + " нет возможности редактировать предмет с id - " + item.getId());
        }

        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.updateItem(item, itemCreateDto)));
    }

    @Override
    public List<ItemDto> searchItems(String query, Long userId) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        return itemRepository
                .searchItems(query.toLowerCase())
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
