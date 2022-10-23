package ru.practicum.shareit.exception;

public class OperationAccessException extends RuntimeException {
    public OperationAccessException(String message) {
        super(message);
    }

    public OperationAccessException(Long id) {
        super(String.format("Operation is prohibited for user with ID = ", id));
    }
}
