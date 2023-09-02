package ru.practicum.ewm.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    private List<String> uris;

    private boolean unique;
}
