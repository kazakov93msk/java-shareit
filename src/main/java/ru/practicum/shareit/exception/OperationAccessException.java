package ru.practicum.shareit.exception;

public class OperationAccessException extends RuntimeException {
    public OperationAccessException(String message) {
        super(message);
    }

    public OperationAccessException(Long id) {
        super(String.format("Operation is prohibited for user with ID = %d", id));
    }

    public OperationAccessException(String clsName, Long userId, Long objectId) {
        super(String.format("User (id=%d) is not the owner of the %s (id=%d)",
                userId, clsName, objectId));
    }
}
