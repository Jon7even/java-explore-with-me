package ru.practicum.ewm.stats.setup;

import ru.practicum.ewm.stats.dto.HitCreateTO;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class GenericTests {
    protected LocalDateTime start;
    protected LocalDateTime end;
    protected List<String> uris;
    protected HitCreateTO hitOne;
    protected HitCreateTO hitSecond;

    protected void initHitDTO() {
        hitOne = HitCreateTO.builder()
                .app("ewm-test-1")
                .uri("test/test/test/1")
                .ip("11.11.11.11")
                .timestamp(LocalDateTime.now())
                .build();

        hitSecond = HitCreateTO.builder()
                .app("ewm-test-2")
                .uri("test/test/test/2")
                .ip("22.22.22.22")
                .timestamp(LocalDateTime.now().plusHours(1))
                .build();
    }

    protected void initVariables() {
        start = LocalDateTime.now();
        end = start.plusHours(1);
        uris = Collections.emptyList();
    }

}
