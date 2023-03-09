package ru.practicum.shareit.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class AlreadyExistsException extends DataIntegrityViolationException {
    public AlreadyExistsException(String clsName, Long id) {
        super(String.format("%s with ID = %d not found", clsName, id));
    }
}
