package ru.practicum.ewm.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NamesExceptions {
    public static final String DUPLICATE_EMAIL = "could not execute statement; SQL [n/a]; constraint [uq_email]; " +
            "nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement";

    public static final String DUPLICATE_CATEGORY = "could not execute statement; SQL [n/a]; constraint [uq_name]; " +
            "nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement";

    public static final String DUPLICATE_TITLE = "could not execute statement; SQL [n/a]; constraint [uq_title]; " +
            "nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement";

    public static final String EVENT_DATE_BEFORE_DURATION = "Field: eventDate. Error: must be before 2 hours.";

    public static final String START_AFTER_END = "Error: range: start after end.";

    public static final String EVENT_DATE_BEFORE_ADMIN = "Field: eventDate. Error: must be before 1 hours.";

    public static final String EVENT_UPDATE_REJECTED = "Only pending or canceled events can be changed";

    public static final String EVENT_CONFIRM_REJECTED = "Only pending events can be changed";

    public static final String EVENT_NOT_PUBLISHED = "I can't send a request to an unpublished event. " +
            "Right state: PUBLISHED";

    public static final String EVENT_PUBLISHED_REJECTED = "Cannot publish the event because it's incorrect status " +
            "Right state: PUBLISHED";

    public static final String REJECTED_REQUEST_INITIATOR = "You cannot send request to participate in your event";

    public static final String EVENT_IS_FULL = "You cannot participate in the event. Count participant is full";

    public static final String EVENT_IS_FULL_COUNT = "You cannot participate in the event. Count participant is full." +
            " Count of available seats is ";

    public static final String REQUEST_ALREADY_EXIST = "Such a request already exists.";

    public static final String CONFIRM_NOT_REQUIRED = "Confirm for request not required. This is Public.";

    public static final String USER_NOT_INITIATOR = "You are not the initiator of the event";

    public static final String CATEGORY_ALREADY_USED = "The category is not empty";

    public static final String INCORRECT_STATUS = "Incorrect status for requests. Right state: CONFIRMED, REJECTED";

    public static final String REQUESTS_ALREADY_CONFIRMED = "This request(s) already confirmed";

    public static final String EVENTS_NOT_EXIST = "Events with such IDs do not exist";
}
