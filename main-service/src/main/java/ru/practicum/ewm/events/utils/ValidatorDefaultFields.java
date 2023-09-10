package ru.practicum.ewm.events.utils;

import lombok.experimental.UtilityClass;

import java.util.Objects;

import static ru.practicum.ewm.config.CommonConfig.*;

@UtilityClass
public class ValidatorDefaultFields {
    public boolean requestModeration(Boolean requestModeration) {
        return Objects.requireNonNullElse(requestModeration, DEFAULT_FIELD_RQS_MODERATION);
    }

    public boolean paid(Boolean paid) {
        return Objects.requireNonNullElse(paid, DEFAULT_FIELD_PAID);
    }

    public int limitParticipant(Integer participantLimit) {
        return Objects.requireNonNullElse(participantLimit, DEFAULT_FIELD_PARTICIPANT);
    }
}
