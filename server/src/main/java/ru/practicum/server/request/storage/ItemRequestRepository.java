package ru.practicum.server.request.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequesterIdNot(Long userId);
}
