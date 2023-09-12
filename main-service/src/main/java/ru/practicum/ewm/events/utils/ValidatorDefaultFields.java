package ru.practicum.ewm.events.utils;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.exception.IncorrectMadeRequestException;

import java.time.LocalDateTime;
import java.util.Objects;

import static ru.practicum.ewm.config.CommonConfig.*;
import static ru.practicum.ewm.constants.NamesExceptions.EVENT_DATE_BEFORE_ADMIN;

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

    public void checkEventDateForAdmin(LocalDateTime eventDate) {
        if (eventDate != null) {
            if (eventDate.isBefore(LocalDateTime.now().plus(ADMIN_CONSTRAIN_TIME))) {
                throw new IncorrectMadeRequestException(EVENT_DATE_BEFORE_ADMIN);
            }
        }
    }

}
