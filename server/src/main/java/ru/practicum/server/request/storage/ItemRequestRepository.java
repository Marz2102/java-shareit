package ru.practicum.server.request.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.server.request.model.ItemRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequesterIdNot(Long userId);

    List<ItemRequest> findAllByRequesterId(Long userId);
}
