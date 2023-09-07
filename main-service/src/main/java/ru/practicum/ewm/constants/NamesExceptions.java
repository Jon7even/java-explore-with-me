package ru.practicum.ewm.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NamesExceptions {
    public static final String DUPLICATE_EMAIL = "could not execute statement; SQL [n/a]; constraint [uq_email]; " +
            "nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement";

    public static final String DUPLICATE_CATEGORY = "could not execute statement; SQL [n/a]; constraint [uq_name]; " +
            "nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement";
}
