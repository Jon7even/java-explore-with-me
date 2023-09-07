package ru.practicum.ewm.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@UtilityClass
public class ConverterPage {
    public PageRequest getPageRequest(int from, int size, Optional<Sort> sort) {
        boolean isExistParamSort = sort.isPresent();
        int pageResponse = from / size;

        if (isExistParamSort) {
            return PageRequest.of(pageResponse, size, sort.get());
        } else {
            return PageRequest.of(pageResponse, size);
        }
    }

}
