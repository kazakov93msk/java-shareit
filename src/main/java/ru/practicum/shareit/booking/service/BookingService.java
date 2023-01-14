package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.property.BookingState;

import java.util.List;

public interface BookingService {
    List<Booking> findAllByBookerId(Long bookerId, BookingState state);

    List<Booking> findAllByItemsOwnerId(Long ownerId, BookingState state);

    Booking findBookingById(Long userId, Long bookingId);

    Booking approveBookingById(Long userId, Long bookingId, Boolean decide);

    Booking createBooking(Long userId, Booking booking);
}
