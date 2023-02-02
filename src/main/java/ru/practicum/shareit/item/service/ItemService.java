package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<Item> findByUserId(Long userId, Long from, Integer size);

    Item findById(Long itemId);

    List<Item> searchByText(String text, Long from, Integer size);

    Item create(Long ownerId, Item item);

    Item update(Long userId, Long itemId, Item item);

    void deleteById(Long userId, Long itemId);

    Comment createComment(Long itemId, Long userId, Comment comment);
}
