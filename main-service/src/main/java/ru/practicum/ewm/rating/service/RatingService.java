package ru.practicum.ewm.rating.service;

import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.rating.model.RatingSort;

import java.util.List;

public interface RatingService {
    void addLikeByEventId(Long userId, Long eventId, Boolean isPositive);

    void removeLikeByEventId(Long userId, Long eventId);

    List<EventShortDto> getTopEventsBySortAndPages(RatingSort sort, Integer from, Integer size);
}
