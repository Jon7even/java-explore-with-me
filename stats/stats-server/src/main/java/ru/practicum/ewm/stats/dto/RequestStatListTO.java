package ru.practicum.ewm.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestStatListTO {
    private LocalDateTime start;
    private LocalDateTime end;
    private List<String> uris;
    private boolean unique;
}
