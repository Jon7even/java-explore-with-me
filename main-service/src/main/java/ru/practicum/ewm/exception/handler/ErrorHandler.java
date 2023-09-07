package ru.practicum.ewm.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.exception.main.ApplicationException;
import ru.practicum.ewm.exception.model.ApiError;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ApplicationException.class)
    protected ResponseEntity<Object> handleApplicationException(ApplicationException exception) {
        HttpStatus responseStatus = exception.getHttpStatus();
        String message = exception.getMessage();

        if (responseStatus.is4xxClientError()) {
            log.warn(message);
        } else if (responseStatus.is5xxServerError()) {
            log.error(message);
        } else {
            log.debug(message);
        }

        return ResponseEntity.status(exception.getHttpStatus())
                .body(exception.getResponseException());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException exception) {
        log.error(exception.getMessage());

        ApiError responseException = ApiError.builder()
                .errors(Arrays.asList(exception.getSuppressedFields()))
                .status("BAD_REQUEST")
                .reason("Incorrectly made request.")
                .message(Objects.requireNonNull(exception.getFieldError()).getDefaultMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseException);
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
