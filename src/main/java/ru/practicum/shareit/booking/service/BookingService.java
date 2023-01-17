package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.property.BookingState;

import java.util.List;

public interface BookingService {
    List<Booking> findAllByBookerId(Long bookerId, BookingState state);

    List<Booking> findAllByItemsOwnerId(Long ownerId, BookingState state);

    Booking findById(Long userId, Long bookingId);

    Booking approveById(Long userId, Long bookingId, Boolean decide);

    Booking create(Long userId, Booking booking);
}
