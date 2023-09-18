package ru.practicum.ewm.stats.exception;

import org.springframework.http.HttpStatus;

public class IncorrectMadeRequestException extends ApplicationException {
    public IncorrectMadeRequestException(String message) {
        super("BAD_REQUEST", "Incorrectly made request.", message, HttpStatus.BAD_REQUEST);
    }
}

