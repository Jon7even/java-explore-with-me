package ru.practicum.ewm.controllers.requests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.requests.model.RequestStatus;
import ru.practicum.ewm.setup.GenericControllerEvents;

import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.constants.EndpointsPaths.EVENT_PRIVATE;
import static ru.practicum.ewm.constants.EndpointsPaths.REQUEST_PRIVATE;
import static ru.practicum.ewm.constants.NamesExceptions.*;

public class RequestPrivateControllerTest extends GenericControllerEvents {
    @Test
    @DisplayName("Добавить запрос на участие в событии и принять/отклонить его [confirmRequestByInitiator]")
    void shouldCreateRequestAndConfirmAndReject_thenStatus200and409() throws Exception {
        eventService.createEvent(newEventDtoFieldsDefault, FIRST_ID);
        eventService.createEvent(newEventDtoParticipantLimitTen, FIRST_ID);
        newEventDtoParticipantLimitTen.setParticipantLimit(1);
        eventService.createEvent(newEventDtoParticipantLimitTen, FIRST_ID);

        mockMvc.perform(post(REQUEST_PRIVATE, FIRST_ID)
                        .param("eventId", String.valueOf(FIRST_ID))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value("CONFLICT"))
                .andExpect(jsonPath("reason").value("Integrity constraint has been violated."))
                .andExpect(jsonPath("message").value(EVENT_NOT_PUBLISHED))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        UpdateEventAdminRequest eventDTOConfirm = UpdateEventAdminRequest.builder()
                .stateAction(EventState.PUBLISH_EVENT)
                .build();
        eventService.confirmEvent(FIRST_ID, eventDTOConfirm);

        mockMvc.perform(post(REQUEST_PRIVATE, FIRST_ID)
                        .param("eventId", String.valueOf(FIRST_ID))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value("CONFLICT"))
                .andExpect(jsonPath("reason").value("Integrity constraint has been violated."))
                .andExpect(jsonPath("message").value(REJECTED_REQUEST_INITIATOR))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        mockMvc.perform(post(REQUEST_PRIVATE, SECOND_ID)
                        .param("eventId", String.valueOf(FIRST_ID))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("status").value(RequestStatus.CONFIRMED.toString()));

        eventService.confirmEvent(SECOND_ID, eventDTOConfirm);
        mockMvc.perform(post(REQUEST_PRIVATE, SECOND_ID)
                        .param("eventId", String.valueOf(SECOND_ID))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("status").value(RequestStatus.PENDING.toString()));

        mockMvc.perform(post(REQUEST_PRIVATE, SECOND_ID)
                        .param("eventId", String.valueOf(SECOND_ID))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value("CONFLICT"))
                .andExpect(jsonPath("reason").value("Integrity constraint has been violated."))
                .andExpect(jsonPath("message").value(REQUEST_ALREADY_EXIST))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        EventRequestStatusUpdateRequest patchDto = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(FIRST_ID))
                .status(RequestStatus.REJECTED)
                .build();

        mockMvc.perform(patch(EVENT_PRIVATE + "/{eventId}/requests", FIRST_ID, FIRST_ID)
                        .content(objectMapper.writeValueAsString(patchDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value("CONFLICT"))
                .andExpect(jsonPath("reason").value("Integrity constraint has been violated."))
                .andExpect(jsonPath("message").value(CONFIRM_NOT_REQUIRED))
                .andExpect(jsonPath("timestamp").value(notNullValue()));

        patchDto.setRequestIds(List.of(SECOND_ID));
        mockMvc.perform(patch(EVENT_PRIVATE + "/{eventId}/requests", FIRST_ID, SECOND_ID)
                        .content(objectMapper.writeValueAsString(patchDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rejectedRequests.[0].status").value(RequestStatus.REJECTED.toString()));

        eventService.confirmEvent((SECOND_ID + 1L), eventDTOConfirm);
        mockMvc.perform(post(REQUEST_PRIVATE, SECOND_ID)
                        .param("eventId", String.valueOf(SECOND_ID + 1L))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("status").value(RequestStatus.PENDING.toString()));

        patchDto.setRequestIds(List.of((SECOND_ID + 1)));
        patchDto.setStatus(RequestStatus.CONFIRMED);
        mockMvc.perform(patch(EVENT_PRIVATE + "/{eventId}/requests", FIRST_ID, 3L)
                        .content(objectMapper.writeValueAsString(patchDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.confirmedRequests.[0].status").value(
                        RequestStatus.CONFIRMED.toString())
                );

        mockMvc.perform(patch(EVENT_PRIVATE + "/{eventId}/requests", FIRST_ID, 3L)
                        .content(objectMapper.writeValueAsString(patchDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("status").value("CONFLICT"))
                .andExpect(jsonPath("reason").value("Integrity constraint has been violated."))
                .andExpect(jsonPath("message").value(EVENT_IS_FULL_COUNT + "0"))
                .andExpect(jsonPath("timestamp").value(notNullValue()));
    }

}
