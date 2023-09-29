package ru.practicum.ewm.rating.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.exception.IncorrectMadeRequestException;
import ru.practicum.ewm.rating.model.RatingSort;

@Slf4j
@Component
public class ConverterRatingSort implements Converter<String, RatingSort> {
    @Override
    public RatingSort convert(String value) {
        try {
            return RatingSort.valueOf(value.toUpperCase());
        } catch (RuntimeException e) {
            log.error("An attempt was made to insert an incorrect Rating Sort [{}]", value);
            throw new IncorrectMadeRequestException("UNSUPPORTED_SORT_RATING");
        }
    }
}
