package ru.practicum.ewm.exception.main;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static ru.practicum.ewm.constants.Constants.DATE_TIME_HIT;

@Builder
@Getter
@Setter
public class ApplicationException extends RuntimeException {
    private String status;

    private String reason;

    private String message;

    @JsonFormat(pattern = DATE_TIME_HIT)
    private LocalDateTime timestamp;

    @JsonIgnore
    private HttpStatus httpStatus;

    public ApplicationException(String status, String reason, String message, HttpStatus httpStatus) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.httpStatus = httpStatus;
    }

}
