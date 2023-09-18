package ru.practicum.ewm.stats.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

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
