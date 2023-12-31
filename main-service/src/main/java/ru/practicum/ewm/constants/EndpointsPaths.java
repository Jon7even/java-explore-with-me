package ru.practicum.ewm.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EndpointsPaths {
    public static final String EVENT_ADMIN = "/admin/events";

    public static final String EVENT_PRIVATE = "/users/{userId}/events";

    public static final String EVENT_PUBLIC = "/events";

    public static final String REQUEST_PRIVATE = "/users/{userId}/requests";

    public static final String COMPILATIONS_ADMIN = "/admin/compilations";

    public static final String COMPILATIONS_PUBLIC = "/compilations";

    public static final String USERS_ADMIN = "/admin/users";

    public static final String CATEGORY_ADMIN = "/admin/categories";

    public static final String CATEGORY_PUBLIC = "/categories";
}
