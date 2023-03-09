package ru.practicum.shareit.exception;

public class WrongBookingStateException extends IllegalArgumentException {

    public WrongBookingStateException(String state) {
        super(String.format("Unknown state: %s", state));
    }
}
