package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;
import ru.practicum.ewm.exception.main.ApplicationException;

public class EntityNotFoundException extends ApplicationException {
    public EntityNotFoundException(String message) {
        super("NOT_FOUND", "The required object was not found.", message, HttpStatus.NOT_FOUND);
    }
}

