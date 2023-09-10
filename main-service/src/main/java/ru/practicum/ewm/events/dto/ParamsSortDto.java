package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.events.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.constants.DateTimeFormat.DATE_TIME_DEFAULT;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ParamsSortDto {
    private List<Long> users;

    private List<EventState> states;

    private List<Long> categories;

    @JsonFormat(pattern = DATE_TIME_DEFAULT)
    private LocalDateTime rangeStart;

    @JsonFormat(pattern = DATE_TIME_DEFAULT)
    private LocalDateTime rangeEnd;

    private Integer from;

    private Integer size;
}
