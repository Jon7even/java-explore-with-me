package ru.practicum.ewm.events.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static ru.practicum.ewm.constants.NamesExceptions.EVENT_DATE_BEFORE_DURATION;

@Target({FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = EventDateTimeFieldValidator.class)
public @interface EventDateTimeField {
    String message() default EVENT_DATE_BEFORE_DURATION;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
