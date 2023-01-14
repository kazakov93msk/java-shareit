package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.property.BookingState;
import ru.practicum.shareit.booking.property.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRep;
    private final UserService userService;


    @Override
    public List<Booking> findAllByBookerId(Long bookerId, BookingState state) {
        userService.findUserById(bookerId);
        return filterUserBookingsByState(bookerId, state, false);
    }

    @Override
    public List<Booking> findAllByItemsOwnerId(Long ownerId, BookingState state) {
        userService.findUserById(ownerId);
        return filterUserBookingsByState(ownerId, state, true);
    }

    @Override
    public Booking findBookingById(Long userId, Long bookingId) {
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
    public Booking approveBookingById(Long userId, Long bookingId, Boolean isPositiveDecision) {
        Booking booking = bookingRep.findById(bookingId).orElseThrow(
                () -> new NotFoundException(Booking.class.getName(), bookingId)
        );
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new OperationAccessException(Item.class.getSimpleName(), userId, booking.getItem().getId());
        }
        if (!BookingStatus.WAITING.equals(booking.getStatus())) {
            throw new NotAvailableException("The booking decision has already been made.");
        }
        booking.setStatus(isPositiveDecision ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return booking;
    }

    @Override
    @Transactional
    public Booking createBooking(Long userId, Booking booking) {
        booking.setStatus(BookingStatus.WAITING);
        if (booking.getItem().getOwner().getId().equals(userId)) {
            throw new OperationAccessException("The owner cannot be a booker.");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationDataException("The start time cannot be less then now.");
        }
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().equals(booking.getStart())) {
            throw new ValidationDataException("The end time cannot be less or equals than the start time.");
        }
        if (!booking.getItem().getAvailable()) {
            throw new NotAvailableException(Item.class.getSimpleName(), booking.getItem().getId());
        }
        return bookingRep.save(booking);
    }

    private List<Booking> filterUserBookingsByState(Long userId, BookingState state, Boolean isOwner) {
        switch (state) {
            case ALL:
                return bookingRep.findByUserId(userId, isOwner);
            case WAITING:
            case REJECTED:
                BookingStatus bookingStatus = BookingStatus.valueOf(state.toString());
                return bookingRep.findByUserIdAndStatus(userId, isOwner, bookingStatus);
            case CURRENT:
                return bookingRep.findByUserCurrent(userId, isOwner, LocalDateTime.now());
            case PAST:
                return bookingRep.findByUserPast(userId, isOwner, LocalDateTime.now());
            case FUTURE:
                return bookingRep.findByUserFuture(userId, isOwner, LocalDateTime.now());
            default:
                throw new WrongBookingStateException("Unknown state: " + state);
        }
    }
}
