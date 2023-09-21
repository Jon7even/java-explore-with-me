package ru.practicum.ewm.controllers.rating;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.setup.GenericControllerEvents;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.constants.EndpointsPaths.EVENT_PRIVATE;
import static ru.practicum.ewm.constants.NamesExceptions.EVENT_IS_YOUR;

public class FeatureRatingTest extends GenericControllerEvents {
    @Test
    @DisplayName("Лайк/дизлайк должен поставиться без ошибок [addLikeById]")
    void shouldAddLikeAndDislike_thenStatus204() throws Exception {
        EventFullDto event1 = eventService.createEvent(newEventDtoStandard, firstId);
        EventFullDto event2 = eventService.createEvent(newEventDtoRequestModerationFalse, firstId);
        EventFullDto event3 = eventService.createEvent(newEventDtoFieldsDefault, firstId);
        EventFullDto event4 = eventService.createEvent(newEventDtoPaidTrue, firstId);
        EventFullDto event5 = eventService.createEvent(newEventDtoParticipantLimitTen, firstId);
        userService.createUser(thirdNewUserRequest);

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", secondId, event1.getId())
                        .param("isPositive", "true"))
                .andExpect(status().isNoContent());

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", secondId, event1.getId())
                        .param("isPositive", "true"))
                .andExpect(status().isNoContent());

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", secondId, event2.getId())
                        .param("isPositive", "false"))
                .andExpect(status().isNoContent());

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", secondId, event2.getId())
                        .param("isPositive", "false"))
                .andExpect(status().isNoContent());

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", secondId, event3.getId())
                        .param("isPositive", "true"))
                .andExpect(status().isNoContent());

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", secondId, event4.getId())
                        .param("isPositive", "false"))
                .andExpect(status().isNoContent());

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", secondId, event5.getId())
                        .param("isPositive", "true"))
                .andExpect(status().isNoContent());

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", secondId, event5.getId())
                        .param("isPositive", "false"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Лайк должен удалиться по [ID] [removeLikeById]")
    void shouldDeleteLikeById_thenStatus204() throws Exception {
        EventFullDto event1 = eventService.createEvent(newEventDtoStandard, firstId);

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", secondId, event1.getId())
                        .param("isPositive", "true"))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete(EVENT_PRIVATE + "/{eventId}/like", secondId, event1.getId()))
                .andExpect(status().isNoContent());

        EventFullDto event2 = eventService.createEvent(newEventDtoRequestModerationFalse, firstId);

        mockMvc.perform(put(EVENT_PRIVATE + "/{eventId}/like", secondId, event2.getId())
                        .param("isPositive", "false"))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete(EVENT_PRIVATE + "/{eventId}/like", secondId, event2.getId()))
                .andExpect(status().isNoContent());


    }

    @Test
    @DisplayName("Лайк/дизлайк не должен поставиться [addLikeById]")
    void shouldNotAddLikeAndDislike_thenStatus404And409() throws Exception {
        EventFullDto event1 = eventService.createEvent(newEventDtoStandard, firstId);

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

}
