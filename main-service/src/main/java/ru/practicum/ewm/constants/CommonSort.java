package ru.practicum.ewm.constants;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;

@UtilityClass
public class CommonSort {
    public static final Sort DEFAULT_SORT_BY_ID = Sort.by(Sort.Direction.ASC, "id");
}
