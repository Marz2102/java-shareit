package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();

        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getCommentator().getName());
        commentDto.setCreated(comment.getCreated());

        return commentDto;
    }

    public static Comment toComment(CommentCreateDto commentCreateDto) {
        Comment comment = new Comment();
        comment.setText(commentCreateDto.getText());
        return comment;
    }
}
