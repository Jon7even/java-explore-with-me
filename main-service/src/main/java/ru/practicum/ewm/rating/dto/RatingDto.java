package ru.practicum.ewm.rating.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.users.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.ewm.constants.DateTimeFormat.DATE_TIME_DEFAULT;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RatingDto {
    private UserShortDto liker;

    private Boolean is_positive;

    @JsonFormat(pattern = DATE_TIME_DEFAULT)
    private LocalDateTime dateTime;
}
