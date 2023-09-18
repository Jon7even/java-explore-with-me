package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.events.model.EventSort;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.constants.DateTimeFormat.DATE_TIME_DEFAULT;

@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PublicParamsSortDto {
    private String text;

    private List<Integer> categories;

    private Boolean paid;

    @JsonFormat(pattern = DATE_TIME_DEFAULT)
    private LocalDateTime rangeStart;

    @JsonFormat(pattern = DATE_TIME_DEFAULT)
    private LocalDateTime rangeEnd;

    private Boolean onlyAvailable;

    private EventSort sort;

    private Integer from;

    private Integer size;

    private HttpServletRequest request;
}
