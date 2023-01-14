package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.OutputItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @GetMapping
    public List<OutputItemDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("GET: Get all items where owner ID = {}.", userId);
        return ItemMapper.mapToItemDto(itemService.findByUserId(userId), userId);
    }

    @GetMapping("/{itemId}")
    public OutputItemDto findItemById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId
    ) {
        log.debug("GET: Get item by ID = {}.", itemId);
        return ItemMapper.mapToItemDto(itemService.findItemById(itemId), userId);
    }

    @GetMapping("/search")
    public List<OutputItemDto> searchItemByParams(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam String text
    ) {
        log.debug("GET: Search item containing text '{}' in title or description.", text);
        return ItemMapper.mapToItemDto(itemService.searchItemsByText(text), userId);
    }

    @PostMapping
    public OutputItemDto createItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody InputItemDto itemDto
    ) {
        log.debug("POST: Create item {} with owner ID = {}.", itemDto, userId);
        return ItemMapper.mapToItemDto(itemService.createItem(userId, ItemMapper.mapToItem(itemDto)), userId);
    }

    @PatchMapping("/{itemId}")
    public OutputItemDto updateItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody InputItemDto itemDto
    ) {
        log.debug("PATCH: Update item {} where owner ID = {}.", itemDto, userId);
        Item item = ItemMapper.mapToItem(itemDto);
        return ItemMapper.mapToItemDto(itemService.updateItem(userId, itemId, item), userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId
    ) {
        log.debug("DELETE: Delete item with ID = {} where owner ID = {}.", itemId, userId);
        itemService.deleteItemById(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody CommentDto commentDto,
            @PathVariable Long itemId
    ) {
        log.debug("POST: Create comment {} for item ID = {}.", commentDto, itemId);
        User author = userService.findUserById(userId);
        Item item = itemService.findItemById(itemId);
        Comment comment = CommentMapper.mapToComment(commentDto, author, item);
        return CommentMapper.mapToCommentDto(itemService.createComment(itemId, userId, comment));
    }
}
