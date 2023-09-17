package ru.practicum.ewm.stats.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.stats.dto.constants.Constants.DATE_TIME_HIT;

@Data
@Builder
@RequiredArgsConstructor
public class ApiError {
    private final List<String> errors;

    private final String status;

    private final String reason;

    private final String message;

    @JsonFormat(pattern = DATE_TIME_HIT)
    private final LocalDateTime timestamp;
}
