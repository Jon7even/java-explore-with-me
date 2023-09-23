package ru.practicum.ewm.utils;

import ru.practicum.ewm.events.model.EventEntity;
import ru.practicum.ewm.rating.utils.CalculateRating;

import java.util.Comparator;

public class EventTopRatingComparator implements Comparator<EventEntity> {
    @Override
    public int compare(EventEntity e1, EventEntity e2) {
        return CalculateRating.getRate(e1.getLikes().size(), e1.getDisLikes().size())
                - CalculateRating.getRate(e2.getLikes().size(), e2.getDisLikes().size());
    }
}
