package ru.practicum.ewm.rating.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static ru.practicum.ewm.constants.DateTimeFormat.DATE_TIME_DEFAULT;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RatingDto {
    private Long liker;

    @JsonFormat(pattern = DATE_TIME_DEFAULT)
    private LocalDateTime dateTime;
}
