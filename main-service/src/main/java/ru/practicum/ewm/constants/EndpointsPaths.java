package ru.practicum.ewm.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EndpointsPaths {
    public static final String EVENT_ADMIN = "/admin/events";

    public static final String EVENT_PRIVATE = "/users/{userId}/events";

    public static final String EVENT_PUBLIC = "/events";

    public static final String REQUEST_PRIVATE = "/users/{userId}/requests";
}
