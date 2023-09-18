package ru.practicum.ewm.controllers.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.setup.GenericControllerEvents;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.config.CommonConfig.DEFAULT_INITIAL_STATE;
import static ru.practicum.ewm.constants.DateTimeFormat.DATE_TIME_DEFAULT;
import static ru.practicum.ewm.constants.EndpointsPaths.EVENT_ADMIN;
import static ru.practicum.ewm.constants.EndpointsPaths.EVENT_PRIVATE;
import static ru.practicum.ewm.constants.NamesExceptions.*;
import static ru.practicum.ewm.events.model.EventState.*;

public class EventAdminControllerTest extends GenericControllerEvents {
    @Test
    @DisplayName("Подтвердить или отклонить событие админом и получить full DTO [confirm]")
    void shouldConfirmOrRejectEventByAdmin_thenStatus200and404and409() throws Exception {
        EventFullDto event1 = eventService.createEvent(newEventDtoFieldsDefault, firstId);
        eventService.createEvent(newEventDtoStandard, firstId);

        UpdateEventAdminRequest eventDTOConfirm = UpdateEventAdminRequest.builder()
                .annotation("This path confirm for Test")
                .build();

        mockMvc.perform(patch(EVENT_ADMIN + "/{eventId}", event1.getId())
                        .content(objectMapper.writeValueAsString(eventDTOConfirm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("annotation").value(eventDTOConfirm.getAnnotation()))
                .andExpect(jsonPath("title").value(event1.getTitle()));

        categoryService.createCategory(secondNewCategoryDto);
        eventDTOConfirm.setCategory(secondIdInteger);
        mockMvc.perform(patch(EVENT_ADMIN + "/{eventId}", event1.getId())
                        .content(objectMapper.writeValueAsString(eventDTOConfirm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("category.id").value(eventDTOConfirm.getCategory()))
                .andExpect(jsonPath("title").value(event1.getTitle()));

        eventDTOConfirm.setDescription("This path confirm for Test Description");
        mockMvc.perform(patch(EVENT_ADMIN + "/{eventId}", event1.getId())
                        .content(objectMapper.writeValueAsString(eventDTOConfirm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("description").value(eventDTOConfirm.getDescription()))
                .andExpect(jsonPath("title").value(event1.getTitle()));

        eventDTOConfirm.setEventDate(now.plusDays(1));
        mockMvc.perform(patch(EVENT_ADMIN + "/{eventId}", event1.getId())
                        .content(objectMapper.writeValueAsString(eventDTOConfirm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("eventDate").value(eventDTOConfirm.getEventDate()
                        .format(DateTimeFormatter.ofPattern(DATE_TIME_DEFAULT))))
                .andExpect(jsonPath("title").value(event1.getTitle()));

        eventDTOConfirm.setRequestModeration(false);
        mockMvc.perform(patch(EVENT_ADMIN + "/{eventId}", event1.getId())
                        .content(objectMapper.writeValueAsString(eventDTOConfirm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("requestModeration").value(eventDTOConfirm.getRequestModeration()))
                .andExpect(jsonPath("title").value(event1.getTitle()));

        eventDTOConfirm.setTitle("This path confirm for Test Title");
        mockMvc.perform(patch(EVENT_ADMIN + "/{eventId}", event1.getId())
                        .content(objectMapper.writeValueAsString(eventDTOConfirm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title").value(eventDTOConfirm.getTitle()));

        eventDTOConfirm.setStateAction(REJECT_EVENT);
        mockMvc.perform(patch(EVENT_ADMIN + "/{eventId}", event1.getId())
                        .content(objectMapper.writeValueAsString(eventDTOConfirm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("state").value(CANCELED.toString()))
                .andExpect(jsonPath("publishedOn").value(nullValue()));

        eventDTOConfirm.setEventDate(now);
        mockMvc.perform(patch(EVENT_ADMIN + "/{eventId}", event1.getId())
                        .content(objectMapper.writeValueAsString(eventDTOConfirm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("BAD_REQUEST"))
                .andExpect(jsonPath("reason").value("Incorrectly made request."))
                .andExpect(jsonPath("message").value(EVENT_DATE_BEFORE_ADMIN))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        eventDTOConfirm.setEventDate(now.plusDays(1));
        eventDTOConfirm.setStateAction(SEND_TO_REVIEW);
        mockMvc.perform(patch(EVENT_PRIVATE + "/{eventId}", firstId, event1.getId())
                        .content(objectMapper.writeValueAsString(eventDTOConfirm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("state").value(DEFAULT_INITIAL_STATE.toString()));

        eventDTOConfirm.setStateAction(PUBLISHED);
        mockMvc.perform(patch(EVENT_ADMIN + "/{eventId}", event1.getId())
                        .content(objectMapper.writeValueAsString(eventDTOConfirm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value("CONFLICT"))
                .andExpect(jsonPath("reason").value("Integrity constraint has been violated."))
                .andExpect(jsonPath("message").value(EVENT_CONFIRM_REJECTED))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        eventDTOConfirm.setStateAction(PUBLISH_EVENT);
        mockMvc.perform(patch(EVENT_ADMIN + "/{eventId}", event1.getId())
                        .content(objectMapper.writeValueAsString(eventDTOConfirm))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("state").value(PUBLISHED.toString()))
                .andExpect(jsonPath("publishedOn").value(notNullValue()));
    }

    @Test
    @DisplayName("Получить админом список событий по выборке [getShortListByUserId]")
    void shouldGetPageableFullListEventByAdminAndParamsSorts_thenStatus200and409() throws Exception {
        eventService.createEvent(newEventDtoStandard, firstId);
        eventService.createEvent(newEventDtoRequestModerationFalse, firstId);
        eventService.createEvent(newEventDtoFieldsDefault, secondId);
        eventService.createEvent(newEventDtoPaidTrue, secondId);
        eventService.createEvent(newEventDtoParticipantLimitTen, secondId);

        mockMvc.perform(get(EVENT_ADMIN)
                        .param("rangeStart", LocalDateTime.now().plusHours(1)
                                .format(DateTimeFormatter.ofPattern(DATE_TIME_DEFAULT)))
                        .param("rangeEnd", LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern(DATE_TIME_DEFAULT))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("BAD_REQUEST"))
                .andExpect(jsonPath("reason").value("Incorrectly made request."))
                .andExpect(jsonPath("message").value(START_AFTER_END))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        mockMvc.perform(get(EVENT_ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));

        mockMvc.perform(get(EVENT_PRIVATE, firstId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(get(EVENT_PRIVATE, secondId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        mockMvc.perform(get(EVENT_ADMIN)
                        .param("states", PENDING.toString())
                        .param("users", "1, 2")
                        .param("categories", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));

        mockMvc.perform(get(EVENT_ADMIN)
                        .param("states", PENDING.toString())
                        .param("users", "1")
                        .param("categories", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

}
