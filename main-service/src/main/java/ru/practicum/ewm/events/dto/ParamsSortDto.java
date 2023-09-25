package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.events.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.constants.DateTimeFormat.DATE_TIME_DEFAULT;


@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ParamsSortDto {
    private List<Long> users;

    private List<EventState> states;

    private List<Integer> categories;

    @JsonFormat(pattern = DATE_TIME_DEFAULT)
    private LocalDateTime rangeStart;

    @JsonFormat(pattern = DATE_TIME_DEFAULT)
    private LocalDateTime rangeEnd;

    private Integer from;

    private Integer size;
}
