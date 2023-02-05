package ru.practicum.shareit.util;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;

import static java.time.LocalDateTime.now;
import static ru.practicum.shareit.util.ItemTestUtil.getItem;
import static ru.practicum.shareit.util.UserTestUtil.USER_NAME;
import static ru.practicum.shareit.util.UserTestUtil.getUser;

public class CommentTestUtil {
    public static final Long COMMENT_ID = 1L;
    public static final String COMMENT_TEXT = "CommentText";

    public static Comment getComment() {
        return new Comment(COMMENT_ID, COMMENT_TEXT, getItem(), getUser(), now());
    }

    public static CommentDto getCommentDto() {
        return new CommentDto(null, COMMENT_TEXT, USER_NAME, now());
    }

    public static CommentDto getOutputCommentDto() {
        return CommentMapper.mapToCommentDto(getComment());
    }
}
