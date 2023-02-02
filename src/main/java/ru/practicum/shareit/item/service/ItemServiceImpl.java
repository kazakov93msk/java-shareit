package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.utility.PageableBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    public List<Item> findByUserId(Long userId, Long from, Integer size) {
        userService.validateExistence(userId);
        Pageable pageable = PageableBuilder.getPageable(from, size, itemRep.ID_ASC);
        List<Item> items = itemRep.findAllByOwnerId(userId, pageable).getContent();
        return setLastAndNextBookingsAndComments(items);
    }

    @Override
    public Item findById(Long itemId) {
        Item item = itemRep.findById(itemId).orElseThrow(
                () -> new NotFoundException(Item.class.getSimpleName(), itemId)
        );
        item.setComments(commentRep.findByItemId(itemId, commentRep.CREATED_DESC));
        return setLastAndNextBookings(item);
    }

    @Override
    public List<Item> searchByText(String text, Long from, Integer size) {
        Pageable pageable = PageableBuilder.getPageable(from, size, itemRep.ID_ASC);
        return itemRep.search(text.toLowerCase(), pageable).getContent();
    }

    @Override
    @Transactional
    public Item create(Long ownerId, Item item) {
        item.setOwner(userService.findById(ownerId));
        return itemRep.save(item);
    }

    @Override
    @Transactional
    public Item update(Long userId, Long itemId, Item newItem) {
        Item oldItem = itemRep.findById(itemId).orElseThrow(
                () -> new NotFoundException(Item.class.getSimpleName(), itemId)
        );
        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new OperationAccessException(Item.class.getSimpleName(), userId, itemId);
        }

        if (newItem.getName() != null && !newItem.getName().isBlank()) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null && !newItem.getDescription().isBlank()) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }

        return oldItem;
    }

    @Override
    @Transactional
    public void deleteById(Long userId, Long itemId) {
        Item item = findById(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new OperationAccessException(Item.class.getSimpleName(), userId, itemId);
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
                bookingRep.findTopByItemIdAndStatusAndStartLessThanEqual(
                        item.getId(),
                        BookingStatus.APPROVED,
                        LocalDateTime.now(),
                        bookingRep.START_DESC)
        );
        item.setNextBooking(
                bookingRep.findTopByItemIdAndStatusAndStartAfter(
                        item.getId(),
                        BookingStatus.APPROVED,
                        LocalDateTime.now(),
                        bookingRep.START_DESC)
        );
        return item;
    }

    private List<Item> setLastAndNextBookingsAndComments(List<Item> items) {
        Map<Item, List<Booking>> bookings = bookingRep.findByItemInAndStatus(
                        items, BookingStatus.APPROVED, bookingRep.START_DESC).stream()
                .collect(Collectors.groupingBy(Booking::getItem, Collectors.toList()));
        Map<Item, List<Comment>> comments = commentRep.findByItemIn(items, commentRep.CREATED_DESC).stream()
                .collect(Collectors.groupingBy(Comment::getItem, Collectors.toList()));

        for (Item item : items) {
            item.setComments(comments.get(item));
            if (bookings.get(item) != null) {
                item.setLastBooking(bookings.get(item).stream()
                        .filter(booking -> !booking.getStart().isAfter(LocalDateTime.now()))
                        .findFirst().orElse(null)
                );
                item.setNextBooking(bookings.get(item).stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .reduce((first, last) -> last).orElse(null)
                );
            }
        }
        return items;
    }

}
