package ru.practicum.ewm.controllers.rating;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.rating.model.RatingSort;
import ru.practicum.ewm.setup.GenericControllerEvents;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.constants.EndpointsPaths.EVENT_PRIVATE;
import static ru.practicum.ewm.constants.EndpointsPaths.EVENT_PUBLIC;
import static ru.practicum.ewm.constants.NamesExceptions.EVENT_IS_YOUR;
import static ru.practicum.ewm.constants.NamesExceptions.EVENT_NOT_PUBLISHED;
import static ru.practicum.ewm.events.model.EventState.PUBLISH_EVENT;

public class FeatureRatingTest extends GenericControllerEvents {
    @Test
    @DisplayName("Лайк/дизлайк должен поставиться без ошибок [addLikeById]")
    void shouldAddLikeAndDislike_thenStatus204() throws Exception {
        EventFullDto event1 = eventService.createEvent(newEventDtoStandard, firstId);
        EventFullDto event2 = eventService.createEvent(newEventDtoRequestModerationFalse, firstId);
        EventFullDto event3 = eventService.createEvent(newEventDtoFieldsDefault, firstId);
        userService.createUser(thirdNewUserRequest);

        UpdateEventAdminRequest eventDTOConfirm = UpdateEventAdminRequest.builder()
                .stateAction(PUBLISH_EVENT)
                .build();
        eventService.confirmEvent(event1.getId(), eventDTOConfirm);
        eventService.confirmEvent(event2.getId(), eventDTOConfirm);
        eventService.confirmEvent(event3.getId(), eventDTOConfirm);

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", secondId, event1.getId())
                        .param("isPositive", "true"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(EVENT_PRIVATE + "/{eventId}", firstId, event1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(event1.getId()))
                .andExpect(jsonPath("likes").value(hasSize(1)))
                .andExpect(jsonPath("likes[0].liker").value(secondId))
                .andExpect(jsonPath("disLikes").value(hasSize(0)));

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", secondId, event1.getId())
                        .param("isPositive", "true"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(EVENT_PRIVATE + "/{eventId}", firstId, event1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(event1.getId()))
                .andExpect(jsonPath("likes").value(hasSize(1)))
                .andExpect(jsonPath("likes[0].liker").value(secondId))
                .andExpect(jsonPath("disLikes").value(hasSize(0)));

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", secondId, event2.getId())
                        .param("isPositive", "false"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(EVENT_PRIVATE + "/{eventId}", firstId, event2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(event2.getId()))
                .andExpect(jsonPath("likes").value(hasSize(0)))
                .andExpect(jsonPath("disLikes").value(hasSize(1)))
                .andExpect(jsonPath("disLikes[0].liker").value(secondId));

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", secondId, event2.getId())
                        .param("isPositive", "false"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(EVENT_PRIVATE + "/{eventId}", firstId, event2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(event2.getId()))
                .andExpect(jsonPath("likes").value(hasSize(0)))
                .andExpect(jsonPath("disLikes").value(hasSize(1)))
                .andExpect(jsonPath("disLikes[0].liker").value(secondId));

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", secondId, event3.getId())
                        .param("isPositive", "true"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(EVENT_PRIVATE + "/{eventId}", firstId, event3.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(event3.getId()))
                .andExpect(jsonPath("likes").value(hasSize(1)))
                .andExpect(jsonPath("likes[0].liker").value(secondId))
                .andExpect(jsonPath("disLikes").value(hasSize(0)));

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", secondId, event3.getId())
                        .param("isPositive", "false"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(EVENT_PRIVATE + "/{eventId}", firstId, event3.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(event3.getId()))
                .andExpect(jsonPath("likes").value(hasSize(0)))
                .andExpect(jsonPath("disLikes").value(hasSize(1)))
                .andExpect(jsonPath("disLikes[0].liker").value(secondId));

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", thirdId, event3.getId())
                        .param("isPositive", "true"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(EVENT_PRIVATE + "/{eventId}", firstId, event3.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(event3.getId()))
                .andExpect(jsonPath("likes").value(hasSize(1)))
                .andExpect(jsonPath("likes[0].liker").value(thirdId))
                .andExpect(jsonPath("disLikes").value(hasSize(1)))
                .andExpect(jsonPath("disLikes[0].liker").value(secondId));
    }

    @Test
    @DisplayName("Лайк должен удалиться по [ID] [removeLikeById]")
    void shouldDeleteLikeById_thenStatus204() throws Exception {
        EventFullDto event1 = eventService.createEvent(newEventDtoStandard, firstId);

        UpdateEventAdminRequest eventDTOConfirm = UpdateEventAdminRequest.builder()
                .stateAction(PUBLISH_EVENT)
                .build();
        eventService.confirmEvent(event1.getId(), eventDTOConfirm);

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", secondId, event1.getId())
                        .param("isPositive", "true"))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete(EVENT_PRIVATE + "/{eventId}/like", secondId, event1.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(EVENT_PRIVATE + "/{eventId}", firstId, event1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(event1.getId()))
                .andExpect(jsonPath("likes").value(hasSize(0)))
                .andExpect(jsonPath("disLikes").value(hasSize(0)));

        EventFullDto event2 = eventService.createEvent(newEventDtoRequestModerationFalse, firstId);

        eventService.confirmEvent(event2.getId(), eventDTOConfirm);

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", secondId, event2.getId())
                        .param("isPositive", "false"))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete(EVENT_PRIVATE + "/{eventId}/like", secondId, event2.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(EVENT_PRIVATE + "/{eventId}", firstId, event2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(event2.getId()))
                .andExpect(jsonPath("likes").value(hasSize(0)))
                .andExpect(jsonPath("disLikes").value(hasSize(0)));
    }

    @Test
    @DisplayName("Лайк/дизлайк не должен поставиться [addLikeById]")
    void shouldNotAddLikeAndDislike_thenStatus404And409() throws Exception {
        EventFullDto event1 = eventService.createEvent(newEventDtoStandard, firstId);

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", secondId, event1.getId())
                        .param("isPositive", "true"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value("CONFLICT"))
                .andExpect(jsonPath("reason").value("Integrity constraint has been violated."))
                .andExpect(jsonPath("message").value(EVENT_NOT_PUBLISHED))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        UpdateEventAdminRequest eventDTOConfirm = UpdateEventAdminRequest.builder()
                .stateAction(PUBLISH_EVENT)
                .build();
        eventService.confirmEvent(event1.getId(), eventDTOConfirm);

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", (secondId + 99L), event1.getId())
                        .param("isPositive", "true"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value("NOT_FOUND"))
                .andExpect(jsonPath("reason").value("The required object was not found."))
                .andExpect(jsonPath("message")
                        .value("User with id=" + (secondId + 99L) + " was not found"))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", secondId, (secondId + 99L))
                        .param("isPositive", "true"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value("NOT_FOUND"))
                .andExpect(jsonPath("reason").value("The required object was not found."))
                .andExpect(jsonPath("message")
                        .value("Event with id=" + (secondId + 99L) + " was not found"))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", firstId, event1.getId())
                        .param("isPositive", "true"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value("CONFLICT"))
                .andExpect(jsonPath("reason").value("Integrity constraint has been violated."))
                .andExpect(jsonPath("message").value(EVENT_IS_YOUR))
                .andExpect(jsonPath("timestamp").value(notNullValue()));
    }

    @Test
    @DisplayName("Получить публичный рейтинг событий по параметру необходимой сортировки [getTopBySortAndPages]")
    void shouldGetPageableShortListEventBySort_thenStatus200() throws Exception {
        userService.createUser(thirdNewUserRequest);
        thirdNewUserRequest.setEmail("email@test4");
        userService.createUser(thirdNewUserRequest);
        thirdNewUserRequest.setEmail("email@test5");
        userService.createUser(thirdNewUserRequest);
        thirdNewUserRequest.setEmail("email@test6");
        userService.createUser(thirdNewUserRequest);

        EventFullDto event1 = eventService.createEvent(newEventDtoStandard, firstId);
        EventFullDto event2 = eventService.createEvent(newEventDtoRequestModerationFalse, firstId);
        EventFullDto event3 = eventService.createEvent(newEventDtoFieldsDefault, firstId);
        EventFullDto event4 = eventService.createEvent(newEventDtoPaidTrue, firstId);
        EventFullDto event5 = eventService.createEvent(newEventDtoParticipantLimitTen, firstId);

        UpdateEventAdminRequest eventDTOConfirm = UpdateEventAdminRequest.builder()
                .stateAction(PUBLISH_EVENT)
                .build();
        eventService.confirmEvent(event1.getId(), eventDTOConfirm);
        eventService.confirmEvent(event2.getId(), eventDTOConfirm);
        eventService.confirmEvent(event3.getId(), eventDTOConfirm);
        eventService.confirmEvent(event4.getId(), eventDTOConfirm);
        eventService.confirmEvent(event5.getId(), eventDTOConfirm);

        eventService.addLikeByEventId(2L, event1.getId(), false);

        eventService.addLikeByEventId(2L, event2.getId(), true);
        eventService.addLikeByEventId(3L, event2.getId(), true);
        eventService.addLikeByEventId(4L, event2.getId(), true);
        eventService.addLikeByEventId(5L, event2.getId(), true);
        eventService.addLikeByEventId(6L, event2.getId(), true);

        eventService.addLikeByEventId(2L, event3.getId(), true);
        eventService.addLikeByEventId(3L, event3.getId(), true);
        eventService.addLikeByEventId(4L, event3.getId(), true);

        eventService.addLikeByEventId(5L, event4.getId(), true);

        mockMvc.perform(get(EVENT_PUBLIC + "/top")
                        .param("sort", RatingSort.LIKES.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(event2.getId()))
                .andExpect(jsonPath("$[0].annotation").value(event2.getAnnotation()))
                .andExpect(jsonPath("$[1].id").value(event3.getId()))
                .andExpect(jsonPath("$[1].annotation").value(event3.getAnnotation()))
                .andExpect(jsonPath("$[2].id").value(event4.getId()))
                .andExpect(jsonPath("$[2].annotation").value(event4.getAnnotation()));

        eventService.addLikeByEventId(2L, event1.getId(), true);

        eventService.addLikeByEventId(2L, event2.getId(), false);
        eventService.addLikeByEventId(3L, event2.getId(), false);
        eventService.addLikeByEventId(4L, event2.getId(), false);
        eventService.addLikeByEventId(5L, event2.getId(), false);
        eventService.addLikeByEventId(6L, event2.getId(), false);

        eventService.addLikeByEventId(2L, event3.getId(), false);
        eventService.addLikeByEventId(3L, event3.getId(), false);
        eventService.addLikeByEventId(4L, event3.getId(), false);

        eventService.addLikeByEventId(5L, event4.getId(), false);

        mockMvc.perform(get(EVENT_PUBLIC + "/top")
                        .param("sort", RatingSort.DISLIKES.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(event2.getId()))
                .andExpect(jsonPath("$[0].annotation").value(event2.getAnnotation()))
                .andExpect(jsonPath("$[0].likes").value(hasSize(0)))
                .andExpect(jsonPath("$[1].id").value(event3.getId()))
                .andExpect(jsonPath("$[1].annotation").value(event3.getAnnotation()))
                .andExpect(jsonPath("$[1].likes").value(hasSize(0)))
                .andExpect(jsonPath("$[2].id").value(event4.getId()))
                .andExpect(jsonPath("$[2].annotation").value(event4.getAnnotation()))
                .andExpect(jsonPath("$[2].likes").value(hasSize(0)));

        eventService.addLikeByEventId(2L, event1.getId(), true);
        eventService.addLikeByEventId(3L, event1.getId(), true);
        eventService.addLikeByEventId(4L, event1.getId(), true);
        eventService.addLikeByEventId(5L, event1.getId(), true);
        eventService.addLikeByEventId(6L, event1.getId(), true);

        eventService.addLikeByEventId(2L, event5.getId(), false);
        eventService.addLikeByEventId(3L, event5.getId(), true);
        eventService.addLikeByEventId(4L, event5.getId(), true);
        eventService.addLikeByEventId(5L, event5.getId(), true);
        eventService.addLikeByEventId(6L, event5.getId(), true);

        eventService.addLikeByEventId(2L, event4.getId(), false);
        eventService.addLikeByEventId(3L, event4.getId(), false);
        eventService.addLikeByEventId(4L, event4.getId(), false);
        eventService.addLikeByEventId(5L, event4.getId(), true);
        eventService.addLikeByEventId(6L, event4.getId(), true);

        eventService.addLikeByEventId(2L, event3.getId(), false);
        eventService.addLikeByEventId(3L, event3.getId(), false);
        eventService.addLikeByEventId(4L, event3.getId(), false);
        eventService.addLikeByEventId(5L, event3.getId(), false);
        eventService.addLikeByEventId(6L, event3.getId(), true);

        eventService.addLikeByEventId(2L, event2.getId(), false);
        eventService.addLikeByEventId(3L, event2.getId(), false);
        eventService.addLikeByEventId(4L, event2.getId(), false);
        eventService.addLikeByEventId(5L, event2.getId(), false);
        eventService.addLikeByEventId(6L, event2.getId(), false);

        mockMvc.perform(get(EVENT_PUBLIC + "/top")
                        .param("sort", RatingSort.TOTAL_RATING.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].id").value(event1.getId()))
                .andExpect(jsonPath("$[0].annotation").value(event1.getAnnotation()))
                .andExpect(jsonPath("$[1].id").value(event5.getId()))
                .andExpect(jsonPath("$[1].annotation").value(event5.getAnnotation()))
                .andExpect(jsonPath("$[2].id").value(event4.getId()))
                .andExpect(jsonPath("$[2].annotation").value(event4.getAnnotation()))
                .andExpect(jsonPath("$[3].id").value(event3.getId()))
                .andExpect(jsonPath("$[3].annotation").value(event3.getAnnotation()))
                .andExpect(jsonPath("$[4].id").value(event2.getId()))
                .andExpect(jsonPath("$[4].annotation").value(event2.getAnnotation()));
    }

}
