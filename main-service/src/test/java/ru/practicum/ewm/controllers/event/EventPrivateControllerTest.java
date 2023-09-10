package ru.practicum.ewm.controllers.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.UpdateEventUserRequest;
import ru.practicum.ewm.setup.GenericControllerEvents;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.config.CommonConfig.*;
import static ru.practicum.ewm.constants.DateTimeFormat.DATE_TIME_DEFAULT;
import static ru.practicum.ewm.constants.EndpointsPaths.EVENT_PRIVATE;
import static ru.practicum.ewm.constants.NamesExceptions.EVENT_DATE_BEFORE_DURATION;
import static ru.practicum.ewm.events.model.EventState.*;

public class EventPrivateControllerTest extends GenericControllerEvents {
    @Test
    @DisplayName("Новое событие должно успешно создаться с разными DTO [create]")
    void shouldCreateEvents_thenStatus201() throws Exception {
        mockMvc.perform(post(EVENT_PRIVATE, FIRST_ID)
                        .content(objectMapper.writeValueAsString(newEventDtoStandard))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(FIRST_ID))
                .andExpect(jsonPath("annotation").value(newEventDtoStandard.getAnnotation()))
                .andExpect(jsonPath("confirmedRequests").value(zero))
                .andExpect(jsonPath("createdOn").value(notNullValue()))
                .andExpect(jsonPath("description").value(newEventDtoStandard.getDescription()))
                .andExpect(jsonPath("eventDate").value(
                        newEventDtoStandard.getEventDate().format(DateTimeFormatter.ofPattern(DATE_TIME_DEFAULT))))
                .andExpect(jsonPath("initiator.id").value(FIRST_ID))
                .andExpect(jsonPath("location.lat").value(location.getLat()))
                .andExpect(jsonPath("location.lon").value(location.getLon()))
                .andExpect(jsonPath("paid").value(false))
                .andExpect(jsonPath("participantLimit").value("0"))
                .andExpect(jsonPath("publishedOn").value(nullValue()))
                .andExpect(jsonPath("requestModeration").value(newEventDtoStandard.getRequestModeration()))
                .andExpect(jsonPath("state").value(DEFAULT_INITIAL_STATE.toString()))
                .andExpect(jsonPath("title").value(newEventDtoStandard.getTitle()))
                .andExpect(jsonPath("views").value(zero));

        mockMvc.perform(post(EVENT_PRIVATE, FIRST_ID)
                        .content(objectMapper.writeValueAsString(newEventDtoRequestModerationFalse))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(SECOND_ID))
                .andExpect(jsonPath("annotation").value(newEventDtoRequestModerationFalse.getAnnotation()))
                .andExpect(jsonPath("confirmedRequests").value(zero))
                .andExpect(jsonPath("createdOn").value(notNullValue()))
                .andExpect(jsonPath("description").value(newEventDtoRequestModerationFalse.getDescription()))
                .andExpect(jsonPath("eventDate").value(newEventDtoRequestModerationFalse.getEventDate()
                        .format(DateTimeFormatter.ofPattern(DATE_TIME_DEFAULT))))
                .andExpect(jsonPath("initiator.id").value(FIRST_ID))
                .andExpect(jsonPath("location.lat").value(location.getLat()))
                .andExpect(jsonPath("location.lon").value(location.getLon()))
                .andExpect(jsonPath("paid").value(false))
                .andExpect(jsonPath("participantLimit").value("0"))
                .andExpect(jsonPath("publishedOn").value(nullValue()))
                .andExpect(jsonPath("requestModeration").value(
                        newEventDtoRequestModerationFalse.getRequestModeration()))
                .andExpect(jsonPath("state").value(DEFAULT_INITIAL_STATE.toString()))
                .andExpect(jsonPath("title").value(newEventDtoRequestModerationFalse.getTitle()))
                .andExpect(jsonPath("views").value(zero));

        mockMvc.perform(post(EVENT_PRIVATE, FIRST_ID)
                        .content(objectMapper.writeValueAsString(newEventDtoParticipantLimitTen))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(SECOND_ID + 1))
                .andExpect(jsonPath("annotation").value(newEventDtoParticipantLimitTen.getAnnotation()))
                .andExpect(jsonPath("confirmedRequests").value(zero))
                .andExpect(jsonPath("createdOn").value(notNullValue()))
                .andExpect(jsonPath("description").value(newEventDtoParticipantLimitTen.getDescription()))
                .andExpect(jsonPath("eventDate").value(newEventDtoParticipantLimitTen.getEventDate()
                        .format(DateTimeFormatter.ofPattern(DATE_TIME_DEFAULT))))
                .andExpect(jsonPath("initiator.id").value(FIRST_ID))
                .andExpect(jsonPath("location.lat").value(location.getLat()))
                .andExpect(jsonPath("location.lon").value(location.getLon()))
                .andExpect(jsonPath("paid").value(false))
                .andExpect(jsonPath("participantLimit").value(newEventDtoParticipantLimitTen.getParticipantLimit()))
                .andExpect(jsonPath("publishedOn").value(nullValue()))
                .andExpect(jsonPath("requestModeration").value(
                        newEventDtoParticipantLimitTen.getRequestModeration()))
                .andExpect(jsonPath("state").value(DEFAULT_INITIAL_STATE.toString()))
                .andExpect(jsonPath("title").value(newEventDtoParticipantLimitTen.getTitle()))
                .andExpect(jsonPath("views").value(zero));

        mockMvc.perform(post(EVENT_PRIVATE, FIRST_ID)
                        .content(objectMapper.writeValueAsString(newEventDtoPaidTrue))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(SECOND_ID + 2))
                .andExpect(jsonPath("annotation").value(newEventDtoPaidTrue.getAnnotation()))
                .andExpect(jsonPath("confirmedRequests").value(zero))
                .andExpect(jsonPath("createdOn").value(notNullValue()))
                .andExpect(jsonPath("description").value(newEventDtoPaidTrue.getDescription()))
                .andExpect(jsonPath("eventDate").value(newEventDtoPaidTrue.getEventDate()
                        .format(DateTimeFormatter.ofPattern(DATE_TIME_DEFAULT))))
                .andExpect(jsonPath("initiator.id").value(FIRST_ID))
                .andExpect(jsonPath("location.lat").value(location.getLat()))
                .andExpect(jsonPath("location.lon").value(location.getLon()))
                .andExpect(jsonPath("paid").value(newEventDtoPaidTrue.getPaid()))
                .andExpect(jsonPath("participantLimit").value(zero))
                .andExpect(jsonPath("publishedOn").value(nullValue()))
                .andExpect(jsonPath("requestModeration").value(
                        newEventDtoPaidTrue.getRequestModeration()))
                .andExpect(jsonPath("state").value(DEFAULT_INITIAL_STATE.toString()))
                .andExpect(jsonPath("title").value(newEventDtoPaidTrue.getTitle()))
                .andExpect(jsonPath("views").value(zero));

        mockMvc.perform(post(EVENT_PRIVATE, FIRST_ID)
                        .content(objectMapper.writeValueAsString(newEventDtoFieldsDefault))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(SECOND_ID + 3))
                .andExpect(jsonPath("annotation").value(newEventDtoFieldsDefault.getAnnotation()))
                .andExpect(jsonPath("confirmedRequests").value(zero))
                .andExpect(jsonPath("createdOn").value(notNullValue()))
                .andExpect(jsonPath("description").value(newEventDtoFieldsDefault.getDescription()))
                .andExpect(jsonPath("eventDate").value(newEventDtoFieldsDefault.getEventDate()
                        .format(DateTimeFormatter.ofPattern(DATE_TIME_DEFAULT))))
                .andExpect(jsonPath("initiator.id").value(FIRST_ID))
                .andExpect(jsonPath("location.lat").value(location.getLat()))
                .andExpect(jsonPath("location.lon").value(location.getLon()))
                .andExpect(jsonPath("paid").value(DEFAULT_FIELD_PAID))
                .andExpect(jsonPath("participantLimit").value(DEFAULT_FIELD_PARTICIPANT))
                .andExpect(jsonPath("publishedOn").value(nullValue()))
                .andExpect(jsonPath("requestModeration").value(DEFAULT_FIELD_RQS_MODERATION))
                .andExpect(jsonPath("state").value(DEFAULT_INITIAL_STATE.toString()))
                .andExpect(jsonPath("title").value(newEventDtoFieldsDefault.getTitle()))
                .andExpect(jsonPath("views").value(zero));
    }

    @Test
    @DisplayName("Новое событие не должно создаться [create]")
    void shouldNotCreateEvent_thenStatus400And404() throws Exception {
        newEventDtoParticipantLimitTen.setEventDate(LocalDateTime.now());
        mockMvc.perform(post(EVENT_PRIVATE, FIRST_ID)
                        .content(objectMapper.writeValueAsString(newEventDtoParticipantLimitTen))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("BAD_REQUEST"))
                .andExpect(jsonPath("reason").value("Incorrectly made request."))
                .andExpect(jsonPath("message").value(EVENT_DATE_BEFORE_DURATION))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        newEventDtoFieldsDefault.setAnnotation("testtest");
        mockMvc.perform(post(EVENT_PRIVATE, FIRST_ID)
                        .content(objectMapper.writeValueAsString(newEventDtoFieldsDefault))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("BAD_REQUEST"))
                .andExpect(jsonPath("reason").value("Incorrectly made request."))
                .andExpect(jsonPath("message")
                        .value("Field: annotation. Error: must not go beyond. Min=20, Max=2000 symbols"))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        newEventDtoPaidTrue.setLocation(null);
        mockMvc.perform(post(EVENT_PRIVATE, FIRST_ID)
                        .content(objectMapper.writeValueAsString(newEventDtoPaidTrue))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("BAD_REQUEST"))
                .andExpect(jsonPath("reason").value("Incorrectly made request."))
                .andExpect(jsonPath("message")
                        .value("Field: location. Error: must not be null. Value: null"))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        newEventDtoRequestModerationFalse.setCategory(SECOND_ID_INTEGER);
        mockMvc.perform(post(EVENT_PRIVATE, SECOND_ID)
                        .content(objectMapper.writeValueAsString(newEventDtoRequestModerationFalse))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value("NOT_FOUND"))
                .andExpect(jsonPath("reason").value("The required object was not found."))
                .andExpect(jsonPath("message")
                        .value("Category with id=" + SECOND_ID_INTEGER + " was not found"))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        mockMvc.perform(post(EVENT_PRIVATE, SECOND_ID + 1)
                        .content(objectMapper.writeValueAsString(newEventDtoStandard))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value("NOT_FOUND"))
                .andExpect(jsonPath("reason").value("The required object was not found."))
                .andExpect(jsonPath("message")
                        .value("User with id=" + (SECOND_ID + 1) + " was not found"))
                .andExpect(jsonPath("timestamp").value(notNullValue()));
    }

    @Test
    @DisplayName("Получить владельцем урезанную DTO списка событий со страницами [getShortListByUserId]")
    void shouldGetPageableShortListEventByInitiator_thenStatus200() throws Exception {
        EventFullDto event1 = eventService.createEvent(newEventDtoStandard, FIRST_ID);
        EventFullDto event2 = eventService.createEvent(newEventDtoRequestModerationFalse, FIRST_ID);
        EventFullDto event3 = eventService.createEvent(newEventDtoFieldsDefault, SECOND_ID);

        mockMvc.perform(get(EVENT_PRIVATE, FIRST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(event1.getId()))
                .andExpect(jsonPath("$[0].annotation").value(event1.getAnnotation()))
                .andExpect(jsonPath("$[1].id").value(event2.getId()))
                .andExpect(jsonPath("$[1].annotation").value(event2.getAnnotation()));

        mockMvc.perform(get(EVENT_PRIVATE, FIRST_ID)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(event1.getId()))
                .andExpect(jsonPath("$[0].annotation").value(event1.getAnnotation()));

        mockMvc.perform(get(EVENT_PRIVATE, SECOND_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(event3.getId()))
                .andExpect(jsonPath("$[0].annotation").value(event3.getAnnotation()));
    }

    @Test
    @DisplayName("Получить владельцем full DTO события по id [getFullById]")
    void shouldGetEventByInitiator_thenStatus200and404() throws Exception {
        EventFullDto event1 = eventService.createEvent(newEventDtoStandard, FIRST_ID);
        eventService.createEvent(newEventDtoRequestModerationFalse, FIRST_ID);

        mockMvc.perform(get(EVENT_PRIVATE + "/{eventId}", FIRST_ID, event1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(event1.getId()))
                .andExpect(jsonPath("annotation").value(event1.getAnnotation()))
                .andExpect(jsonPath("confirmedRequests").value(event1.getConfirmedRequests()))
                .andExpect(jsonPath("createdOn").value(notNullValue()))
                .andExpect(jsonPath("description").value(event1.getDescription()))
                .andExpect(jsonPath("eventDate").value(event1.getEventDate()
                        .format(DateTimeFormatter.ofPattern(DATE_TIME_DEFAULT))))
                .andExpect(jsonPath("initiator.id").value(event1.getInitiator().getId()))
                .andExpect(jsonPath("location.lat").value(event1.getLocation().getLat()))
                .andExpect(jsonPath("location.lon").value(event1.getLocation().getLon()))
                .andExpect(jsonPath("paid").value(event1.getPaid()))
                .andExpect(jsonPath("participantLimit").value(event1.getParticipantLimit()))
                .andExpect(jsonPath("publishedOn").value(nullValue()))
                .andExpect(jsonPath("requestModeration").value(event1.getRequestModeration()))
                .andExpect(jsonPath("state").value(event1.getState().toString()))
                .andExpect(jsonPath("title").value(event1.getTitle()))
                .andExpect(jsonPath("views").value(event1.getViews()));

        mockMvc.perform(get(EVENT_PRIVATE + "/{eventId}", (SECOND_ID + 1), event1.getId())
                        .content(objectMapper.writeValueAsString(newEventDtoStandard))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value("NOT_FOUND"))
                .andExpect(jsonPath("reason").value("The required object was not found."))
                .andExpect(jsonPath("message")
                        .value("User with id=" + (SECOND_ID + 1) + " was not found"))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        mockMvc.perform(get(EVENT_PRIVATE + "/{eventId}", SECOND_ID, event1.getId())
                        .content(objectMapper.writeValueAsString(newEventDtoStandard))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value("NOT_FOUND"))
                .andExpect(jsonPath("reason").value("The required object was not found."))
                .andExpect(jsonPath("message")
                        .value("Event with id=" + event1.getId() + " was not found"))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        mockMvc.perform(get(EVENT_PRIVATE + "/{eventId}", FIRST_ID, 3L)
                        .content(objectMapper.writeValueAsString(newEventDtoStandard))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value("NOT_FOUND"))
                .andExpect(jsonPath("reason").value("The required object was not found."))
                .andExpect(jsonPath("message")
                        .value("Event with id=" + 3 + " was not found"))
                .andExpect(jsonPath("timestamp").value(notNullValue()));
    }

    @Test
    @DisplayName("Обновить событие владельцем и получить full DTO [updateById]")
    void shouldUpdateEventByInitiatorId_thenStatus200() throws Exception {
        EventFullDto event1 = eventService.createEvent(newEventDtoFieldsDefault, FIRST_ID);
        eventService.createEvent(newEventDtoStandard, FIRST_ID);

        UpdateEventUserRequest eventDTOUpdate = UpdateEventUserRequest.builder()
                .annotation("This path update for Test")
                .build();

        mockMvc.perform(patch(EVENT_PRIVATE + "/{eventId}", FIRST_ID, event1.getId())
                        .content(objectMapper.writeValueAsString(eventDTOUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("annotation").value(eventDTOUpdate.getAnnotation()))
                .andExpect(jsonPath("title").value(event1.getTitle()));

        categoryService.createCategory(secondNewCategoryDto);
        eventDTOUpdate.setCategory(SECOND_ID_INTEGER);
        mockMvc.perform(patch(EVENT_PRIVATE + "/{eventId}", FIRST_ID, event1.getId())
                        .content(objectMapper.writeValueAsString(eventDTOUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("category.id").value(eventDTOUpdate.getCategory()))
                .andExpect(jsonPath("title").value(event1.getTitle()));

        eventDTOUpdate.setDescription("This path update for Test Description");
        mockMvc.perform(patch(EVENT_PRIVATE + "/{eventId}", FIRST_ID, event1.getId())
                        .content(objectMapper.writeValueAsString(eventDTOUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("description").value(eventDTOUpdate.getDescription()))
                .andExpect(jsonPath("title").value(event1.getTitle()));

        eventDTOUpdate.setEventDate(now.plusDays(1));
        mockMvc.perform(patch(EVENT_PRIVATE + "/{eventId}", FIRST_ID, event1.getId())
                        .content(objectMapper.writeValueAsString(eventDTOUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("eventDate").value(eventDTOUpdate.getEventDate()
                        .format(DateTimeFormatter.ofPattern(DATE_TIME_DEFAULT))))
                .andExpect(jsonPath("title").value(event1.getTitle()));

        eventDTOUpdate.setRequestModeration(false);
        mockMvc.perform(patch(EVENT_PRIVATE + "/{eventId}", FIRST_ID, event1.getId())
                        .content(objectMapper.writeValueAsString(eventDTOUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("requestModeration").value(eventDTOUpdate.getRequestModeration()))
                .andExpect(jsonPath("title").value(event1.getTitle()));

        eventDTOUpdate.setStateAction(SEND_TO_REVIEW);
        mockMvc.perform(patch(EVENT_PRIVATE + "/{eventId}", FIRST_ID, event1.getId())
                        .content(objectMapper.writeValueAsString(eventDTOUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("state").value(DEFAULT_INITIAL_STATE.toString()))
                .andExpect(jsonPath("title").value(event1.getTitle()));

        eventDTOUpdate.setTitle("This path update for Test Title");
        mockMvc.perform(patch(EVENT_PRIVATE + "/{eventId}", FIRST_ID, event1.getId())
                        .content(objectMapper.writeValueAsString(eventDTOUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title").value(eventDTOUpdate.getTitle()));

        eventDTOUpdate.setStateAction(CANCEL_REVIEW);
        mockMvc.perform(patch(EVENT_PRIVATE + "/{eventId}", FIRST_ID, event1.getId())
                        .content(objectMapper.writeValueAsString(eventDTOUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("state").value(CANCELED.toString()));

        eventDTOUpdate.setEventDate(now);
        mockMvc.perform(patch(EVENT_PRIVATE + "/{eventId}", FIRST_ID, event1.getId())
                        .content(objectMapper.writeValueAsString(eventDTOUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("BAD_REQUEST"))
                .andExpect(jsonPath("reason").value("Incorrectly made request."))
                .andExpect(jsonPath("message").value(EVENT_DATE_BEFORE_DURATION))
                .andExpect(jsonPath("timestamp").value(notNullValue()));
    }


}
