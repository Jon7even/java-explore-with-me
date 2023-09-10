package ru.practicum.ewm.events.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.events.model.EventSort;
import ru.practicum.ewm.exception.IncorrectMadeRequestException;

@Slf4j
@Component
public class ConverterEventSort implements Converter<String, EventSort> {
    @Override
    public EventSort convert(String value) {
        try {
            return EventSort.valueOf(value.toUpperCase());
        } catch (RuntimeException e) {
            log.error("An attempt was made to insert an incorrect Event Sort [{}]", value);
            throw new IncorrectMadeRequestException("UNSUPPORTED_SORT_EVENT");
        }
    }
}
