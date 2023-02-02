package ru.practicum.shareit.exception;

public class IllegalPageArgumentException extends RuntimeException {
    public IllegalPageArgumentException(String message) {
        super(message);
    }
}
