package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.exceptions.DataAccessException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BookingService bookingService;

    public ItemServiceImpl(final ItemRepository itemRepository, final CommentRepository
            commentRepository, final UserService userService, final BookingService bookingService) {
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @Override
    public ItemDto getItemDtoById(Long id, Long userId) {
        return ItemMapper.toItemDto(itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Предмет с id - " + id + " не найден")));
    }

    @Override
    public Item getItemById(Long id, Long userId) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Предмет с id - " + id + " не найден"));
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        return itemRepository
                .findByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
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

        Optional<LocalDateTime> lastBooking = bookingService.getLastBookingDate(itemId);
        Optional<LocalDateTime> nextBooking = bookingService.getNextBookingDate(itemId);

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
