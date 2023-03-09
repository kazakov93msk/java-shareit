package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class BookingDateValidator implements ConstraintValidator<StartBeforeEndValidation, BookingDto> {
    @Override
    public void initialize(StartBeforeEndValidation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();

        if (start == null || end == null) {
            return false;
        }

        return end.isAfter(start);
    }
}
