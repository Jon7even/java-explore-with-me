package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.ewm.events.model.EventSort;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.config.CommonConfig.DEFAULT_MONTHS_COUNT;
import static ru.practicum.ewm.constants.DateTimeFormat.DATE_TIME_DEFAULT;

@Builder
@Getter
@ToString
@AllArgsConstructor
public class PublicParamsSortDto {
    @NotNull
    private final String text;

    @NotNull
    private final List<Integer> categories;

    private final Boolean paid;

    @Builder.Default
    @JsonFormat(pattern = DATE_TIME_DEFAULT)
    private LocalDateTime rangeStart = LocalDateTime.now();

    @Builder.Default
    @JsonFormat(pattern = DATE_TIME_DEFAULT)
    private LocalDateTime rangeEnd = LocalDateTime.now().plusMonths(DEFAULT_MONTHS_COUNT);

    @NotNull
    private final Boolean onlyAvailable;

    @NotNull
    private final EventSort sort;

    @NotNull
    private final Integer from;

    @NotNull
    private final Integer size;

    @NotNull
    private final HttpServletRequest request;
}
