package ru.practicum.server.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.server.exception.exceptions.NotFoundException;
import ru.practicum.server.item.mapper.ItemMapper;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.storage.ItemRepository;
import ru.practicum.server.request.RequestMapper;
import ru.practicum.server.request.dto.CreateRequestDto;
import ru.practicum.server.request.dto.FullItemRequestDto;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.item.dto.ItemResponseDto;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.request.storage.ItemRequestRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemRequestServiceImpl(final ItemRequestRepository itemRequestRepository, final ItemRepository itemRepository,
                                  final UserService userService) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public ItemRequestDto createRequest(CreateRequestDto itemRequestDto, Long userId) {
        ItemRequest itemRequest = RequestMapper.toItemRequest(itemRequestDto);
        User user = userService.getUserById(userId);

        itemRequest.setRequester(user);

        return RequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<FullItemRequestDto> getRequests(Long userId) {
        List<FullItemRequestDto> requests = itemRequestRepository.findAll()
                .stream()
                .map(RequestMapper::toFullItemRequestDto)
                .toList();

        if (requests.isEmpty()) {
            return List.of();
        }

        List<Long> requestIds = requests.stream()
                .map(FullItemRequestDto::getId)
                .toList();

        List<Item> items = itemRepository.findAllByRequestIdIn(requestIds);

        Map<Long, List<Item>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .peek(request -> {
                    List<Item> itemResponses = itemsByRequestId.getOrDefault(request.getId(), List.of());
                    List<ItemResponseDto> itemResponseDto = itemResponses.stream().map(ItemMapper::toItemResponseDto).toList();
                    request.setItems(itemResponseDto);
                })
                .sorted(Comparator.comparing(FullItemRequestDto::getCreated).reversed())
                .toList();
    }

    @Override
    public List<ItemRequestDto> getOtherRequests(Long userId) {
        return itemRequestRepository.findAllByRequesterIdNot(userId)
                .stream()
                .map(RequestMapper::toItemRequestDto)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .toList();
    }

    @Override
    public FullItemRequestDto getRequestDtoById(Long id, Long userId) {
        FullItemRequestDto fullItemRequest = RequestMapper.toFullItemRequestDto(itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Запрос с id - " + id + " не найден")));

        List<ItemResponseDto> itemResponses = itemRepository.findAllByRequestId(id)
                .stream()
                .map(ItemMapper::toItemResponseDto)
                .toList();

        fullItemRequest.setItems(itemResponses);
        return fullItemRequest;
    }

    @Override
    public ItemRequest getRequestById(Long id) {
        return itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Запрос с id - " + id + " не найден"));
    }
}
