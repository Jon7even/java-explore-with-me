package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.events.validation.EventDateTimeField;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.ewm.constants.DateTimeFormat.DATE_TIME_DEFAULT;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank(message = "Field: annotation. Error: must not be blank. Value: null")
    @Size(min = 20, max = 2000,
            message = "Field: annotation. Error: must not go beyond. Min=20, Max=2000 symbols")
    private String annotation;

    @NotNull(message = "Field: category. Error: must not be null. Value: null")
    private Integer category;

    @NotBlank(message = "Field: description. Error: must not be blank. Value: null")
    @Size(min = 20, max = 7000,
            message = "Field: description. Error: must not go beyond. Min=20, Max=7000 symbols")
    private String description;

    @JsonFormat(pattern = DATE_TIME_DEFAULT)
    @NotNull(message = "Field: eventDate. Error: must not be null. Value: null")
    @EventDateTimeField()
    private LocalDateTime eventDate;

    @NotNull(message = "Field: location. Error: must not be null. Value: null")
    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    @NotBlank(message = "Field: title. Error: must not be blank. Value: null")
    @Size(min = 3, max = 120,
            message = "Field: title. Error: must not go beyond. Min=3, Max=120 symbols")
    private String title;
}
