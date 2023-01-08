package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.property.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OperationAccessException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRep;
    private final CommentRepository commentRep;
    private final BookingRepository bookingRep;
    private final UserService userService;

    @Override
    public List<Item> findByUserId(Long userId) {
        userService.findUserById(userId);
        return itemRep.findAllByOwner_Id(userId);
    }

    @Override
    public Item findItemById(Long itemId) {
        return itemRep.findById(itemId).orElseThrow(
                () -> new NotFoundException(Item.class.toString(), itemId)
        );
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
    public Item createItem(Long ownerId, Item item) {
        try {
            item.setOwner(userService.findUserById(ownerId));
            return itemRep.save(item);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException(Item.class.toString(), item.getId());
        }
    }

    @Override
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

        return itemRep.save(oldItem);
    }

    @Override
    public void deleteItemById(Long userId, Long itemId) {
        Item item = findItemById(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new OperationAccessException(Item.class.toString(), userId, itemId);
        }
        itemRep.deleteById(itemId);
    }

    @Override
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

    @Override
    public List<Comment> findAllComments() {
        return commentRep.findAll();
    }

    @Override
    public List<CommentDto> findAllCommentsByItemId(Long itemId) {
        return commentRep.findDtoCommentsByItemId(itemId);
    }
}
