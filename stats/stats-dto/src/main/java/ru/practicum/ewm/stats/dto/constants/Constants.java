package ru.practicum.ewm.stats.dto.constants;

import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class Constants {
    public static final String DATE_TIME_HIT = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter DEFAULT_TIME_FORMAT = DateTimeFormatter.ofPattern(DATE_TIME_HIT);
}
