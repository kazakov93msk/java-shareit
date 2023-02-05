package ru.practicum.shareit.item.comment.mapper;

import lombok.experimental.UtilityClass;
import org.mapstruct.Mapper;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
@UtilityClass
public class CommentMapper {

    public static Comment mapToComment(CommentDto commentDto, User author, Item item) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .author(author)
                .item(item)
                .created(commentDto.getCreated())
                .build();
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentDto> mapToCommentDto(List<Comment> comments) {
        return comments.stream().map(CommentMapper::mapToCommentDto).collect(Collectors.toList());
    }
}