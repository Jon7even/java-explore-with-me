package ru.practicum.ewm.stats.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.dto.HitCreateTO;
import ru.practicum.ewm.stats.dto.HitResponseTO;
import ru.practicum.ewm.stats.dto.RequestStatListTO;
import ru.practicum.ewm.stats.model.HitEntity;
import ru.practicum.ewm.stats.projections.HitTO;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class StatMapper {
    public HitEntity toHitEntity(HitCreateTO hitCreateTO) {
        return HitEntity.builder()
                .app(hitCreateTO.getApp())
                .uri(hitCreateTO.getUri())
                .ip(hitCreateTO.getIp())
                .timestamp(hitCreateTO.getTimestamp())
                .build();
    }

    public HitResponseTO toDTOFromProjections(HitTO hitTO) {
        return HitResponseTO.builder()
                .app(hitTO.getApp())
                .uri(hitTO.getUri())
                .hits(hitTO.getHits())
                .build();
    }

    public RequestStatListTO toRequestDTO(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return RequestStatListTO.builder()
                .start(start)
                .end(end)
                .uris(uris)
                .unique(unique)
                .build();
    }

}
