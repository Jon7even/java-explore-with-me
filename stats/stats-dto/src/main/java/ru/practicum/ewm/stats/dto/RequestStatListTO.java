package ru.practicum.ewm.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.stats.dto.constants.Constants.DATE_TIME_HIT;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestStatListTO {
    @JsonFormat(pattern = DATE_TIME_HIT)
    private LocalDateTime start;

    @JsonFormat(pattern = DATE_TIME_HIT)
    private LocalDateTime end;

    private List<String> uris;

    private boolean unique;
}
