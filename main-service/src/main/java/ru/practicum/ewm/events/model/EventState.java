package ru.practicum.ewm.events.model;

public enum EventState {
    PENDING,
    PUBLISHED,
    CANCELED,
    SEND_TO_REVIEW,
    CANCEL_REVIEW,
    PUBLISH_EVENT,
    REJECT_EVENT
}
