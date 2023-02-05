package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.property.BookingState;
import ru.practicum.shareit.booking.property.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OperationAccessException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utility.PageableBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.property.BookingStatus.WAITING;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRep;
    private final UserService userService;

    @Override
    public List<Booking> findAllByBookerId(Long bookerId, BookingState state, Long from, Integer size) {
        userService.findById(bookerId);
        return filterUserBookingsByState(bookerId, state, false, from, size);
    }

    @Override
    public List<Booking> findAllByItemsOwnerId(Long ownerId, BookingState state, Long from, Integer size) {
        userService.findById(ownerId);
        return filterUserBookingsByState(ownerId, state, true, from, size);
    }

    @Override
    public Booking findById(Long userId, Long bookingId) {
        Booking booking = bookingRep.findById(bookingId).orElseThrow(
                () -> new NotFoundException(Booking.class.getName(), bookingId)
        );
        if (!booking.getItem().getOwner().getId().equals(userId)
                && !booking.getBooker().getId().equals(userId)) {
            throw new OperationAccessException(Booking.class.getSimpleName(), userId, bookingId);
        }
        return booking;
    }

    @Override
    @Transactional
    public Booking approveById(Long userId, Long bookingId, Boolean isPositiveDecision) {
        Booking booking = findById(userId, bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new OperationAccessException(Item.class.getSimpleName(), userId, booking.getItem().getId());
        }
        if (!WAITING.equals(booking.getStatus())) {
            throw new NotAvailableException("The booking decision has already been made.");
        }
        booking.setStatus(isPositiveDecision ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return booking;
    }

    @Override
    @Transactional
    public Booking create(Long userId, Booking booking) {
        booking.setStatus(WAITING);
        if (booking.getItem().getOwner().getId().equals(userId)) {
            throw new OperationAccessException("The owner cannot be a booker.");
        }
        if (!booking.getItem().getAvailable()) {
            throw new NotAvailableException(Item.class.getSimpleName(), booking.getItem().getId());
        }
        return bookingRep.save(booking);
    }

    private List<Booking> filterUserBookingsByState(
            Long userId,
            BookingState state,
            Boolean isOwner,
            Long from,
            Integer size
    ) {
        Pageable pageable = PageableBuilder.getPageable(from, size, bookingRep.START_DESC);
        switch (state) {
            case WAITING:
            case REJECTED:
                BookingStatus bookingStatus = BookingStatus.valueOf(state.toString());
                return bookingRep.findByUserIdAndStatus(userId, isOwner, bookingStatus, pageable).getContent();
            case CURRENT:
                return bookingRep.findByUserCurrent(userId, isOwner, LocalDateTime.now(), pageable).getContent();
            case PAST:
                return bookingRep.findByUserPast(userId, isOwner, LocalDateTime.now(), pageable).getContent();
            case FUTURE:
                return bookingRep.findByUserFuture(userId, isOwner, LocalDateTime.now(), pageable).getContent();
            default:
                return bookingRep.findByUserId(userId, isOwner, pageable).getContent();
        }
    }
}
