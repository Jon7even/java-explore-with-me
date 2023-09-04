package ru.practicum.ewm.stats.service;

import ru.practicum.ewm.stats.dto.HitCreateTO;
import ru.practicum.ewm.stats.dto.HitResponseTO;
import ru.practicum.ewm.stats.dto.RequestStatListTO;

import java.util.List;

public interface StatService {
    void createHit(HitCreateTO hitCreateTO);

    List<HitResponseTO> getStats(RequestStatListTO requestStatListTO);
}
