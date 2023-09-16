package ru.practicum.ewm.events.service;

import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(NewEventDto newEventDto, Long userId);

    List<EventShortDto> getShortEventListByUserId(Long userId, Integer from, Integer size);

    EventFullDto getFullEventById(Long userId, Long eventId);

    EventFullDto updateEventById(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventFullDto> getListEventByParams(ParamsSortDto paramsSortDto);

    EventFullDto confirmEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getPublicListShortEventByParams(PublicParamsSortDto paramsSortDto);

    EventFullDto getPublicEventById(Long eventId, HttpServletRequest request);

    List<ParticipationRequestDto> getAllRequestsByInitiator(Long userId, Long eventId);

    EventRequestStatusUpdateResult confirmRequestByInitiator(Long userId, Long eventId,
                                                        EventRequestStatusUpdateRequest updateRequest);
}
