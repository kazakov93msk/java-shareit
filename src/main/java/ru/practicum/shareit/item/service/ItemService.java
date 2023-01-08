package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<Item> findByUserId(Long userId);

    Item findItemById(Long itemId);

    List<Item> searchItemsByText(String text);

    Item createItem(Long ownerId, Item item);

    Item updateItem(Long userId, Long itemId, Item item);

    void deleteItemById(Long userId, Long itemId);

    Comment createComment(Long itemId, Long userId, Comment comment);

    List<Comment> findAllComments();

    List<CommentDto> findAllCommentsByItemId(Long itemId);
}
