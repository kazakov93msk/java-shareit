package ru.practicum.shareit.exception;

public class NotAvailableException extends RuntimeException {
    public NotAvailableException(String message) {
        super(message);
    }

    public NotAvailableException(String clsName, Long id) {
        super(String.format("%s with ID = %d is not available.", clsName, id));
    }
}
