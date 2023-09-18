package ru.practicum.ewm.exception.main;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.practicum.ewm.exception.model.ApiError;

import java.time.LocalDateTime;

@Getter
public class ApplicationException extends RuntimeException {
    private final ApiError responseException;

    private final HttpStatus httpStatus;

    public ApplicationException(String status, String reason, String message, HttpStatus httpStatus) {
        this.responseException = ApiError.builder()
                .status(status)
                .reason(reason)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
        this.httpStatus = httpStatus;
    }

}
