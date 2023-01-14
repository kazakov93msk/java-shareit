package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.property.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OperationAccessException;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRep;
    private final CommentRepository commentRep;
    private final BookingRepository bookingRep;
    private final UserService userService;

    @Override
    public List<Item> findByUserId(Long userId) {
        userService.findUserById(userId);
        return itemRep.findAllByOwner_Id(userId).stream()
                .map(this::setLastAndNextBookings)
                .collect(Collectors.toList());
    }

    @Override
    public Item findItemById(Long itemId) {
        Item item = itemRep.findById(itemId).orElseThrow(
                () -> new NotFoundException(Item.class.toString(), itemId)
        );
        return setLastAndNextBookings(item);
    }

    @Override
    public List<Item> searchItemsByText(String text) {
        if (text != null && !text.isBlank()) {
            return itemRep.search(text.toLowerCase());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public Item createItem(Long ownerId, Item item) {
        item.setOwner(userService.findUserById(ownerId));
        return itemRep.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(Long userId, Long itemId, Item newItem) {
        Item oldItem = itemRep.findById(itemId).orElseThrow(
                () -> new NotFoundException(Item.class.toString(), itemId)
        );
        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new OperationAccessException(Item.class.toString(), userId, itemId);
        }

        oldItem.setName(newItem.getName() != null ? newItem.getName() : oldItem.getName());
        oldItem.setDescription(newItem.getDescription() != null ? newItem.getDescription() : oldItem.getDescription());
        oldItem.setAvailable(newItem.getAvailable() != null ? newItem.getAvailable() : oldItem.getAvailable());

        return oldItem;
    }

    @Override
    @Transactional
    public void deleteItemById(Long userId, Long itemId) {
        Item item = findItemById(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new OperationAccessException(Item.class.toString(), userId, itemId);
        }
        itemRep.deleteById(itemId);
    }

    @Override
    @Transactional
    public Comment createComment(Long itemId, Long userId, Comment comment) {
        Booking booking = bookingRep.findBookingByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now()
        );
        if (booking == null) {
            throw new NotAvailableException(
                    String.format("Booking for Item ID = %d and User ID = %d not found.", itemId, userId)
            );
        }
        comment.setItem(booking.getItem());
        comment.setAuthor(booking.getBooker());
        comment.setCreated(LocalDateTime.now());
        return commentRep.save(comment);
    }

    private Item setLastAndNextBookings(Item item) {
        item.setLastBooking(
                bookingRep.findTopByItem_IdAndBooker_IdIsNotAndEndBeforeOrderByEndDesc(
                        item.getId(), item.getOwner().getId(), LocalDateTime.now()
                ));
        item.setNextBooking(
                bookingRep.findTopByItem_IdAndBooker_IdIsNotAndStartAfterOrderByStart(
                        item.getId(), item.getOwner().getId(), LocalDateTime.now()
                ));
        return item;
    }
}
