package ru.practicum.server.request.service;

import ru.practicum.server.request.dto.CreateRequestDto;
import ru.practicum.server.request.dto.FullItemRequestDto;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(CreateRequestDto itemRequestDto, Long userId);

    List<FullItemRequestDto> getRequests(Long userId);

    List<ItemRequestDto> getOtherRequests(Long userId);

    FullItemRequestDto getRequestDtoById(Long requestId, Long userId);

    ItemRequest getRequestById(Long requestId);
}
