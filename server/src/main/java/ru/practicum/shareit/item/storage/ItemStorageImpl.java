package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item addItem(Item item) {
        item.setId(generateNextId());
        items.put(item.getId(), item);

        return item;
    }

    @Override
    public List<Item> getItems(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .toList();
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);

        return item;
    }

    @Override
    public List<Item> searchItems(String query) {
        return items.values()
                .stream()
                .filter(item -> (item.getName().toLowerCase().contains(query)
                        || item.getDescription().toLowerCase().contains(query)))
                .filter(Item::isAvailable)
                .toList();
    }

    private Long generateNextId() {
        return items
                .keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L) + 1;
    }
}
