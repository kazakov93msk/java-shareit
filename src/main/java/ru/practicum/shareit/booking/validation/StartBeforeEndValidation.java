package ru.practicum.shareit.booking.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookingDateValidator.class)
public @interface StartBeforeEndValidation {
    String message() default "The end time cannot be less or equals than the start time.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
