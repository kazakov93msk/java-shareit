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

import java.util.List;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

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
        userService.findById(userId);
        List<Item> items = itemRep.findAllByOwner_Id(userId);
        return setLastAndNextBookingsAndComments(items);
    }

    @Override
    public Item findById(Long itemId) {
        Item item = itemRep.findById(itemId).orElseThrow(
                () -> new NotFoundException(Item.class.toString(), itemId)
        );
        item.setComments(commentRep.findByItemId(itemId, commentRep.CREATED_DESC));
        return setLastAndNextBookings(item);
    }

    @Override
    public List<Item> searchByText(String text) {
        return itemRep.search(text.toLowerCase());
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
                () -> new NotFoundException(Item.class.toString(), itemId)
        );
        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new OperationAccessException(Item.class.toString(), userId, itemId);
        }

        oldItem.setName(newItem.getName() != null && !newItem.getName().isBlank()
                ? newItem.getName()
                : oldItem.getName()
        );
        oldItem.setDescription(newItem.getDescription() != null && !newItem.getDescription().isBlank()
                ? newItem.getDescription()
                : oldItem.getDescription()
        );
        oldItem.setAvailable(newItem.getAvailable() != null ? newItem.getAvailable() : oldItem.getAvailable());

        return oldItem;
    }

    @Override
    @Transactional
    public void deleteById(Long userId, Long itemId) {
        Item item = findById(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new OperationAccessException(Item.class.toString(), userId, itemId);
        }
        itemRep.deleteById(itemId);
    }

    @Override
    @Transactional
    public Comment createComment(Long itemId, Long userId, Comment comment) {
        Booking booking = bookingRep.findBookingByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, BookingStatus.APPROVED, now()
        );
        if (booking == null) {
            throw new NotAvailableException(
                    String.format("Booking for Item ID = %d and User ID = %d not found.", itemId, userId)
            );
        }
        comment.setItem(booking.getItem());
        comment.setAuthor(booking.getBooker());
        comment.setCreated(now());
        return commentRep.save(comment);
    }

    private Item setLastAndNextBookings(Item item) {
        item.setLastBooking(
                bookingRep.findTopByItemIdAndStatusAndStartLessThanEqual(
                        item.getId(),
                        BookingStatus.APPROVED,
                        now(),
                        bookingRep.START_DESC)
        );
        item.setNextBooking(
                bookingRep.findTopByItemIdAndStatusAndStartAfter(
                                item.getId(),
                                BookingStatus.APPROVED,
                                now(),
                                bookingRep.START_DESC)
        );
        return item;
    }

    private List<Item> setLastAndNextBookingsAndComments(List<Item> items) {
        Map<Item, List<Booking>> bookings = bookingRep.findByItemInAndStatus(
                items, BookingStatus.APPROVED, bookingRep.START_DESC).stream()
                .collect(groupingBy(Booking::getItem, toList()));
        Map<Item, List<Comment>> comments = commentRep.findByItemIn(items, commentRep.CREATED_DESC).stream()
                .collect(groupingBy(Comment::getItem, toList()));

        for (Item item : items) {
            item.setComments(comments.get(item));
            if (bookings.get(item) != null) {
                item.setLastBooking(bookings.get(item).stream()
                        .filter(booking -> booking.getStart().isBefore(now())
                                || booking.getStart().isEqual(now()))
                        .findFirst().orElse(null)
                );
                item.setNextBooking(bookings.get(item).stream()
                        .filter(booking -> booking.getStart().isAfter(now()))
                        .findFirst().orElse(null)
                );
            }
        }
        return items;
    }

}
