package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;
import ru.practicum.ewm.exception.main.ApplicationException;

public class IntegrityConstraintException extends ApplicationException {
    public IntegrityConstraintException(String message) {
        super("CONFLICT", "Integrity constraint has been violated.", message, HttpStatus.CONFLICT);
    }
}