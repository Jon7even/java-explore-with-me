package ru.practicum.ewm.stats.projections;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class HitTO {
    private String app;
    private String uri;
    private Long hits;
}
