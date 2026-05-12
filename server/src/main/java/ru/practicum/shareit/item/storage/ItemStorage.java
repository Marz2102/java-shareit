package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    Optional<Item> findById(Long id);

    List<Item> getItems(Long userId);

    Item addItem(Item item);

    Item updateItem(Item item);

    List<Item> searchItems(String query);
}
