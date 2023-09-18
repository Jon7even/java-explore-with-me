package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;
import ru.practicum.ewm.exception.main.ApplicationException;

public class EntityNotDeletedException extends ApplicationException {
    public EntityNotDeletedException(String message) {
        super("Internal_Server_Error", "The required object was not deleted.",
                message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
