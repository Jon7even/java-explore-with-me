package ru.practicum.ewm.stats.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.stats.exception.ApiError;
import ru.practicum.ewm.stats.exception.ApplicationException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(ApplicationException.class)
    protected ResponseEntity<Object> handleApplicationException(ApplicationException exception) {
        HttpStatus responseStatus = exception.getHttpStatus();
        String message = exception.getMessage();
        log.error(message);

        return ResponseEntity.status(exception.getHttpStatus())
                .body(exception.getResponseException());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleThrowable(final MethodArgumentTypeMismatchException exception) {
        log.error(exception.getMessage());

        ApiError responseException = ApiError.builder()
                .status("INTERNAL_SERVER_ERROR")
                .reason("Incorrectly response of service.")
                .message("Unknown exception")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseException);
    }
}
