package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.ewm.events.model.EventState;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.config.CommonConfig.DEFAULT_MONTHS_COUNT;
import static ru.practicum.ewm.constants.DateTimeFormat.DATE_TIME_DEFAULT;


@Builder
@Getter
@ToString
@AllArgsConstructor
public class ParamsSortDto {
    @NotNull
    private final List<Long> users;

    @NotNull
    private final List<EventState> states;

    @NotNull
    private final List<Integer> categories;

    @Builder.Default
    @JsonFormat(pattern = DATE_TIME_DEFAULT)
    private LocalDateTime rangeStart = LocalDateTime.now();

    @Builder.Default
    @JsonFormat(pattern = DATE_TIME_DEFAULT)
    private LocalDateTime rangeEnd = LocalDateTime.now().plusMonths(DEFAULT_MONTHS_COUNT);

    @NotNull
    private final Integer from;

    @NotNull
    private final Integer size;
}
