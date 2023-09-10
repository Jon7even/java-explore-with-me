package ru.practicum.ewm.events.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

import static ru.practicum.ewm.config.CommonConfig.DEFAULT_CONSTRAIN_TIME;

public class EventDateTimeFieldValidator implements ConstraintValidator<EventDateTimeField, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;
        }
        return !date.isBefore(LocalDateTime.now().plus(DEFAULT_CONSTRAIN_TIME));
    }
}
