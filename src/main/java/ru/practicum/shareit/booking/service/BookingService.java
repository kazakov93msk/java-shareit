package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    List<Booking> findAllByBookerId(Long bookerId, String bookingState);

    List<Booking> findAllByItemsOwnerId(Long ownerId, String bookingState);

    Booking findBookingById(Long userId, Long bookingId);

    Booking approveBookingById(Long userId, Long bookingId, Boolean decide);

    Booking createBooking(Long userId, Booking booking);
}
