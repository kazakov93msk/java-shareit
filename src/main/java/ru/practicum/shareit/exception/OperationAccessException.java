package ru.practicum.shareit.exception;

public class OperationAccessException extends RuntimeException {
    public OperationAccessException(String message) {
        super(message);
    }

    public OperationAccessException(String clsName, Long userId, Long objectId) {
        super(String.format("User (id=%d) is not the owner of the %s (id=%d)",
                userId, clsName, objectId));
    }
}
