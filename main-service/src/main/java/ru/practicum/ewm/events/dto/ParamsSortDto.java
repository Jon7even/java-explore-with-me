package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.events.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.config.CommonConfig.DEFAULT_MONTHS_COUNT;
import static ru.practicum.ewm.constants.DateTimeFormat.DATE_TIME_DEFAULT;



@Builder
@Getter
@ToString
@AllArgsConstructor
public class ParamsSortDto {
    private final List<Long> users;

    private final List<EventState> states;

    private final List<Integer> categories;

    @Builder.Default
    @JsonFormat(pattern = DATE_TIME_DEFAULT)
    private LocalDateTime rangeStart = LocalDateTime.now();

    @Builder.Default
    @JsonFormat(pattern = DATE_TIME_DEFAULT)
    private LocalDateTime rangeEnd = LocalDateTime.now().plusMonths(DEFAULT_MONTHS_COUNT);

    private final Integer from;

    private final Integer size;
}
