package ru.practicum.server.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.booking.storage.BookingRepository;
import ru.practicum.server.exception.exceptions.DataAccessException;
import ru.practicum.server.exception.exceptions.NotAvailableException;
import ru.practicum.server.exception.exceptions.NotFoundException;
import ru.practicum.server.item.dto.*;
import ru.practicum.server.item.mapper.CommentMapper;
import ru.practicum.server.item.mapper.ItemMapper;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.storage.CommentRepository;
import ru.practicum.server.item.storage.ItemRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;

    public ItemServiceImpl(final ItemRepository itemRepository, final CommentRepository
            commentRepository, final UserService userService, final BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Item getItemById(Long id, Long userId) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Предмет с id - " + id + " не найден"));
    }

    @Override
    public ItemDto addItem(ItemCreateDto itemCreateDto, Long userId) {
        Item item = ItemMapper.toItem(itemCreateDto);
        User user = userService.getUserById(userId);

        item.setOwner(user);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long id, ItemCreateDto itemCreateDto, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Предмет с id - " + id + " не найден"));

        if (item.getOwner() != null && !item.getOwner().getId().equals(userId)) {
            throw new DataAccessException("У пользователя с id - " + userId + " нет возможности редактировать предмет с id - " + item.getId());
        }

        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.updateItem(item, itemCreateDto)));
    }

    @Override
    public List<ItemDto> searchItems(String query, Long userId) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        return itemRepository
                .searchItems(query.toLowerCase())
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public CommentDto addComment(CommentCreateDto commentCreateDto, Long userId, Long itemId) {
        Item item = getItemById(itemId, userId);
        User user = userService.getUserById(userId);
        Optional<Booking> booking = bookingRepository.findFirst1BookingByBookerIdAndItemIdAndStatusOrderByEndAsc(userId, itemId, BookingStatus.APPROVED);

        if (booking.isEmpty() || booking.get().getEnd().isAfter(LocalDateTime.now())) {
            throw new NotAvailableException("Вы не можете оставить комментарий для предмета, который не использовали или используете в данный момент");
        }

        Comment comment = CommentMapper.toComment(commentCreateDto);
        comment.setItem(item);
        comment.setCommentator(user);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public ItemCommentsDto getCommentsByItemId(Long itemId, Long userId) {
        Item item = getItemById(itemId, userId);
        User user = userService.getUserById(userId);

        List<CommentDto> comments = commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .toList();

        Optional<LocalDateTime> lastBooking = bookingRepository.findLastBookingDate(itemId, LocalDateTime.now(), userId).map(Booking::getStart);
        Optional<LocalDateTime> nextBooking = bookingRepository.findNextBookingDate(itemId, LocalDateTime.now(), userId).map(Booking::getStart);

        ItemCommentsDto itemCommentsDto = ItemMapper.toItemCommentsDto(item);
        itemCommentsDto.setComments(comments);
        lastBooking.ifPresent(itemCommentsDto::setLastBooking);
        nextBooking.ifPresent(itemCommentsDto::setNextBooking);

        return itemCommentsDto;
    }

    @Override
    public List<ItemCommentsDto> getCommentsForUserItems(Long userId) {
        User user = userService.getUserById(userId);
        List<Item> items = itemRepository.findByOwnerId(userId);
        if (items.isEmpty()) {
            return List.of();
        }
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();

        List<Comment> comments = commentRepository.findAllByItemIdIn(itemIds);
        Map<Long, List<Comment>> commentsByItemId = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return items.stream()
                .map(ItemMapper::toItemCommentsDto)
                .peek(itemCommentsDto -> {
                    List<Comment> itemComments = commentsByItemId.getOrDefault(itemCommentsDto.getId(), List.of());
                    List<CommentDto> commentsDto = itemComments.stream().map(CommentMapper::toCommentDto).toList();
                    itemCommentsDto.setComments(commentsDto);
                })
                .toList();
    }
}
