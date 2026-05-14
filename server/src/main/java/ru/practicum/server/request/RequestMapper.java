package ru.practicum.server.request;

import ru.practicum.server.request.dto.CreateRequestDto;
import ru.practicum.server.request.dto.FullItemRequestDto;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.model.ItemRequest;

public class RequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());

        return itemRequestDto;
    }

    public static FullItemRequestDto toFullItemRequestDto(ItemRequest itemRequest) {
        FullItemRequestDto fullItemRequestDto = new FullItemRequestDto();

        fullItemRequestDto.setId(itemRequest.getId());
        fullItemRequestDto.setDescription(itemRequest.getDescription());
        fullItemRequestDto.setCreated(itemRequest.getCreated());

        return fullItemRequestDto;
    }

    public static ItemRequest toItemRequest(CreateRequestDto createRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(createRequestDto.getDescription());
        return itemRequest;
    }
}
