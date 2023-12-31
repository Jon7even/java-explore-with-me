package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.rating.dto.RatingDto;
import ru.practicum.ewm.users.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.Set;

import static ru.practicum.ewm.constants.DateTimeFormat.DATE_TIME_DEFAULT;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EventFullDto {
    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    @JsonFormat(pattern = DATE_TIME_DEFAULT)
    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(pattern = DATE_TIME_DEFAULT)
    private LocalDateTime eventDate;

    private Long id;

    private UserShortDto initiator;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    @JsonFormat(pattern = DATE_TIME_DEFAULT)
    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private EventState state;

    private String title;

    private Integer views;

    private Set<RatingDto> likes;

    private Set<RatingDto> disLikes;
}
