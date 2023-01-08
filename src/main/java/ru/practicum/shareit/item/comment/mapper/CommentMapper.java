package ru.practicum.shareit.item.comment.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

@Mapper
@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final UserService userService;
    private final ItemService itemService;

    public Comment mapToComment(CommentDto commentDto, Long authorId, Long itemId) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .author(userService.findUserById(authorId))
                .item(itemService.findItemById(itemId))
                .created(commentDto.getCreated())
                .build();
    }

    public CommentDto mapToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
