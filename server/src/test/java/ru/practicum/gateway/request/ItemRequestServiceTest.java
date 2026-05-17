package ru.practicum.gateway.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.server.exception.exceptions.NotFoundException;
import ru.practicum.server.item.storage.ItemRepository;
import ru.practicum.server.request.dto.CreateRequestDto;
import ru.practicum.server.request.dto.FullItemRequestDto;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.model.ItemRequest;

import ru.practicum.server.request.service.ItemRequestServiceImpl;
import ru.practicum.server.request.storage.ItemRequestRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void testCreateRequest() {
        LocalDateTime now = LocalDateTime.now().withNano(0);

        CreateRequestDto createRequestDto = new CreateRequestDto();
        createRequestDto.setDescription("description");

        when(itemRequestRepository.save(any(ItemRequest.class))).thenAnswer(answer -> {
            ItemRequest itemRequest = answer.getArgument(0);
            itemRequest.setId(1L);
            itemRequest.setCreated(now);
            return itemRequest;
        });
        when(userService.getUserById(eq(1L))).thenReturn(new User());

        ItemRequestDto itemRequestDto = itemRequestService.createRequest(createRequestDto, 1L);

        assertThat(itemRequestDto.getId()).isEqualTo(1L);
        assertThat(itemRequestDto.getDescription()).isEqualTo("description");
        assertThat(itemRequestDto.getCreated()).isEqualTo(now);
    }

    @Test
    void testCreateRequestWhenUserNotExists() {
        CreateRequestDto createRequestDto = new CreateRequestDto();
        createRequestDto.setDescription("description");

        when(userService.getUserById(1L)).thenThrow(NotFoundException.class);

        assertThatThrownBy(() -> itemRequestService.createRequest(createRequestDto, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testGetRequests() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        ItemRequest itemRequest = new ItemRequest(1L, "description", null, now);

        when(itemRequestRepository.findAllByRequesterId(eq(1L))).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestIdIn(any(List.class))).thenReturn(List.of());

        List<FullItemRequestDto> result = itemRequestService.getRequests(1L);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().getDescription()).isEqualTo("description");
        assertThat(result.getFirst().getCreated()).isEqualTo(now);
    }

    @Test
    void testGetRequestsWhenRequestNotFound() {
        when(itemRequestRepository.findAllByRequesterId(eq(1L))).thenReturn(List.of());

        List<FullItemRequestDto> result = itemRequestService.getRequests(1L);

        assertThat(result.size()).isEqualTo(0);
        verifyNoMoreInteractions(itemRequestRepository, itemRepository, userService);
    }

    @Test
    void testGetOtherRequests() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        ItemRequest itemRequest = new ItemRequest(1L, "description", null, now);

        when(itemRequestRepository.findAllByRequesterIdNot(eq(1L))).thenReturn(List.of(itemRequest));

        List<ItemRequestDto> result = itemRequestService.getOtherRequests(1L);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().getDescription()).isEqualTo("description");
        assertThat(result.getFirst().getCreated()).isEqualTo(now);
    }

    @Test
    void testGetOtherRequestsWhenRequestNotFound() {
        when(itemRequestRepository.findAllByRequesterIdNot(eq(1L))).thenReturn(List.of());

        List<ItemRequestDto> result = itemRequestService.getOtherRequests(1L);

        assertThat(result.size()).isEqualTo(0);
        verifyNoMoreInteractions(itemRequestRepository, itemRepository, userService);
    }
}
