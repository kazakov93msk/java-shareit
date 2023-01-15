package ru.practicum.shareit.booking.property;

import ru.practicum.shareit.exception.WrongBookingStateException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState checkState(String state) {
        try {
            return BookingState.valueOf(state);
        } catch (Exception e) {
            throw new WrongBookingStateException(state);
        }
    }
}
