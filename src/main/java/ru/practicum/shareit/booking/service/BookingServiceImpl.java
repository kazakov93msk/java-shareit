package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.property.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OperationAccessException;
import ru.practicum.shareit.exception.ValidationDataException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRep;
    private final UserService userService;


    @Override
    public List<Booking> findAllByBookerId(Long bookerId, String bookingState) {
        userService.findUserById(bookerId);
        List<Booking> bookings = bookingRep.findAllByBooker_IdOrderByStartDesc(bookerId);
        return filterBookingByState(bookings, bookingState);
    }

    @Override
    public List<Booking> findAllByItemsOwnerId(Long ownerId, String bookingState) {
        userService.findUserById(ownerId);
        List<Booking> bookings = bookingRep.findAllByItemOwnerIdOrderByStartDesc(ownerId);
        return filterBookingByState(bookings, bookingState);
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
        return bookingRep.save(booking);
    }

    @Override
    public Booking createBooking(Long userId, Booking booking) {
        booking.setStatus(BookingStatus.WAITING);
        System.out.println(booking);
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

    private List<Booking> filterBookingByState(List<Booking> bookings, String bookingState) {
        switch (bookingState) {
            case "ALL":
                return bookings;
            case "WAITING":
            case "REJECTED":
                BookingStatus bookingStatus = BookingStatus.valueOf(bookingState);
                return bookings.stream()
                        .filter(booking -> booking.getStatus().equals(bookingStatus))
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookings.stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                        .filter(booking -> booking.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "PAST":
                return bookings.stream()
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookings.stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unknown state: " + bookingState);
        }
    }
}
