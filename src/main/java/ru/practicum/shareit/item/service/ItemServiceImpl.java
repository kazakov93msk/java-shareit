package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRep;

    @Override
    public List<Item> findAllItems(Long userId) {
        return itemRep.findAllItems(userId);
    }

    @Override
    public Item findItemById(Long userId, Long itemId) {
        if (!itemRep.itemExists(itemId)) {
            throw new NotFoundException(Item.class.toString(), itemId);
        }
        return itemRep.findItemById(itemId).orElseThrow(
                () -> new NotFoundException(Item.class.toString(), itemId)
        );
    }

    @Override
    public List<Item> searchItemsByText(String text) {
        if (text != null && !text.isBlank()) {
            return itemRep.searchItemsByText(text.toLowerCase());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Item createItem(Long userId, Item item) {
        if (item.getId() != null && itemRep.itemExists(item.getId())) {
            throw new AlreadyExistsException(Item.class.toString(), item.getId());
        }
        return itemRep.createItem(userId, item);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        if (!itemRep.itemExists(itemId)) {
            throw new NotFoundException(Item.class.toString(), itemId);
        }
        return itemRep.updateItem(userId, itemId, item);
    }

    @Override
    public void deleteItemById(Long userId, Long itemId) {
        if (!itemRep.itemExists(itemId)) {
            throw new NotFoundException(Item.class.toString(), itemId);
        }
        itemRep.deleteItemById(itemId);
    }
}
