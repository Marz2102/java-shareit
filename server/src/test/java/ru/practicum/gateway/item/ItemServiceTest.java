package ru.practicum.gateway.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.booking.storage.BookingRepository;
import ru.practicum.server.exception.exceptions.NotFoundException;
import ru.practicum.server.item.dto.*;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.service.ItemServiceImpl;
import ru.practicum.server.item.storage.CommentRepository;
import ru.practicum.server.item.storage.ItemRepository;
import ru.practicum.server.request.service.ItemRequestService;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserService;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemRequestService itemRequestService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void testCreateItem() {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("name");
        itemCreateDto.setDescription("description");
        itemCreateDto.setAvailable(true);
        itemCreateDto.setRequestId(1L);

        when(itemRepository.save(any(Item.class))).thenAnswer(answer -> {
            Item item = answer.getArgument(0);
            item.setId(1L);
            return item;
        });

        ItemDto itemDto = itemService.addItem(itemCreateDto, 1L);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("name");
        assertThat(itemDto.getDescription()).isEqualTo("description");
        assertThat(itemDto.isAvailable()).isEqualTo(true);
    }

    @Test
    void testCreateItemWhenUserNotExists() {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("name");
        itemCreateDto.setDescription("description");
        itemCreateDto.setAvailable(true);
        itemCreateDto.setRequestId(1L);

        when(userService.getUserById(1L)).thenThrow(NotFoundException.class);

        assertThatThrownBy(() -> itemService.addItem(itemCreateDto, 1L))
                .isInstanceOf(NotFoundException.class);
        verifyNoMoreInteractions(userService, itemRepository);

    }

    @Test
    void testUpdateItem() {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("NewName");
        itemCreateDto.setDescription("description");

        Item oldItem = new Item();
        oldItem.setId(1L);
        oldItem.setName("name");
        oldItem.setDescription("description");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(oldItem));
        when(itemRepository.save(any(Item.class))).thenReturn(oldItem);

        ItemDto itemDto = itemService.updateItem(1L, itemCreateDto, 2L);

        assertThat(itemDto.getId()).isEqualTo(1);
        assertThat(itemDto.getName()).isEqualTo("NewName");
        assertThat(itemDto.getDescription()).isEqualTo("description");
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void testSearchItems() {
        Item item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(false);

        when(itemRepository.searchItems(any(String.class))).thenReturn(List.of(item));

        List<ItemDto> items = itemService.searchItems("Some item", 1L);

        assertThat(items.size()).isEqualTo(1);
        assertThat(items.getFirst().getId()).isEqualTo(1L);
        assertThat(items.getFirst().getName()).isEqualTo("name");
        assertThat(items.getFirst().getDescription()).isEqualTo("description");
        assertThat(items.getFirst().isAvailable()).isEqualTo(false);
    }

    @Test
    void testSearchItemsWithEmptyQuery() {
        List<ItemDto> items = itemService.searchItems("", 1L);

        assertThat(items.size()).isEqualTo(0);
    }

    @Test
    void testAddComment() {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("text");

        when(commentRepository.save(any(Comment.class))).thenAnswer(answer -> {
            Comment comment = answer.getArgument(0);
            comment.setId(1L);
            return comment;
        });

        when(itemRepository.findById(1L)).thenReturn(Optional.of(new Item()));
        when(bookingRepository.findFirst1BookingByBookerIdAndItemIdAndStatusOrderByEndAsc(1L, 1L, BookingStatus.APPROVED))
                .thenAnswer(answer -> {
                    Booking booking = new Booking();
                    booking.setEnd(LocalDateTime.MIN);
                    return Optional.of(booking);
                });

        when(userService.getUserById(1L)).thenReturn(new User());

        CommentDto commentDto = itemService.addComment(commentCreateDto, 1L, 1L);

        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getText()).isEqualTo("text");
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(itemRepository, times(1)).findById(1L);
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testAddCommentWhenItemNotFound() {
        assertThatThrownBy(() -> itemService.addComment(new CommentCreateDto(), 1L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testGetCommentsByItemId() {
        User user = new User(1L, "user", "1@yandex.ru");
        Item item = new Item(10L, "itemName", "description", true, user, null);
        Comment comment = new Comment(100L, user, item, "text", LocalDateTime.now());

        when(userService.getUserById(1L)).thenReturn(user);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(10L)).thenReturn(List.of(comment));

        ItemCommentsDto comments = itemService.getCommentsByItemId(10L, 1L);

        assertThat(comments.getId()).isEqualTo(10L);
        assertThat(comments.getName()).isEqualTo("itemName");
        assertThat(comments.getDescription()).isEqualTo("description");
        assertThat(comments.getComments().size()).isEqualTo(1);
        assertThat(comments.getComments().getFirst().getId()).isEqualTo(100L);
        assertThat(comments.getComments().getFirst().getText()).isEqualTo("text");
        assertThat(comments.getComments().getFirst().getAuthorName()).isEqualTo("user");
    }

}
