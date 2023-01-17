package ru.practicum.shareit.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String clsName, Long id) {
        super(String.format("%s with ID = %d not found", clsName, id));
    }
}
