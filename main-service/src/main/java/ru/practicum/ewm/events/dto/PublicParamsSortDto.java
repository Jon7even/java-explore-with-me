package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.ewm.events.model.EventSort;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.config.CommonConfig.DEFAULT_MONTHS_COUNT;
import static ru.practicum.ewm.constants.DateTimeFormat.DATE_TIME_DEFAULT;

@Builder
@Getter
@ToString
@AllArgsConstructor
public class PublicParamsSortDto {
    private final String text;

    private final List<Integer> categories;

    private final Boolean paid;

    @Builder.Default
    @JsonFormat(pattern = DATE_TIME_DEFAULT)
    private LocalDateTime rangeStart = LocalDateTime.now();

    @Builder.Default
    @JsonFormat(pattern = DATE_TIME_DEFAULT)
    private LocalDateTime rangeEnd = LocalDateTime.now().plusMonths(DEFAULT_MONTHS_COUNT);

    private final Boolean onlyAvailable;

    private final EventSort sort;

    private final Integer from;

    private final Integer size;

    private final HttpServletRequest request;
}
