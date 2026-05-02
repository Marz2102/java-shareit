package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(Long userId);

    @Query("SELECT i FROM Item as i WHERE " +
            "(LOWER(i.name) LIKE CONCAT('%', ?1, '%') OR " +
            "LOWER(i.description) LIKE CONCAT('%', ?1, '%')) " +
            "AND i.available = true")
    List<Item> searchItems(String query);
}
