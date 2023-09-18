package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;
import ru.practicum.ewm.exception.main.ApplicationException;

public class IncorrectMadeRequestException extends ApplicationException {
    public IncorrectMadeRequestException(String message) {
        super("BAD_REQUEST", "Incorrectly made request.", message, HttpStatus.BAD_REQUEST);
    }
}
