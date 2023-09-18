package ru.practicum.ewm.exception.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.constants.DateTimeFormat.DATE_TIME_DEFAULT;

@Data
@Builder
@RequiredArgsConstructor
public class ApiError {
    private final List<String> errors;

    private final String status;

    private final String reason;

    private final String message;

    @JsonFormat(pattern = DATE_TIME_DEFAULT)
    private final LocalDateTime timestamp;
}
