package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WrongBookingStatusException extends IllegalArgumentException {
    public WrongBookingStatusException(String message) {
        super(message);
        log.error(message);
    }
}
