package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @GetMapping
    public List<ItemDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("GET: Get all items where owner ID = {}.", userId);
        return itemService.findByUserId(userId).stream()
                .map(item -> itemMapper.mapToItemDto(item, userId))
                .collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.debug("GET: Get item by ID = {}.", itemId);
        return itemMapper.mapToItemDto(itemService.findItemById(itemId), userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemByParams(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam String text) {
        log.debug("GET: Search item containing text '{}' in title or description.", text);
        return itemService.searchItemsByText(text).stream()
                .map(item -> itemMapper.mapToItemDto(item, userId))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.debug("POST: Create item {} with owner ID = {}.", itemDto, userId);
        return itemMapper.mapToItemDto(itemService.createItem(userId, itemMapper.mapToItem(itemDto)), userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto
    ) {
        log.debug("PATCH: Update item {} where owner ID = {}.", itemDto, userId);
        Item item = itemMapper.mapToItem(itemDto);
        return itemMapper.mapToItemDto(itemService.updateItem(userId, itemId, item), userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.debug("DELETE: Delete item with ID = {} where owner ID = {}.", itemId, userId);
        itemService.deleteItemById(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody CommentDto commentDto,
            @PathVariable Long itemId) {
        log.debug("POST: Create comment {} for item ID = {}.", commentDto, itemId);
        Comment comment = commentMapper.mapToComment(commentDto, userId, itemId);
        return commentMapper.mapToCommentDto(itemService.createComment(itemId, userId, comment));
    }

    @GetMapping("comments")
    public List<CommentDto> getAllComments() {
        return itemService.findAllComments().stream()
                .map(commentMapper::mapToCommentDto)
                .collect(Collectors.toList());
    }

    @GetMapping("comments/{itemId}")
    public List<CommentDto> getAllComments(@PathVariable Long itemId) {
        return itemService.findAllCommentsByItemId(itemId);
    }
}
