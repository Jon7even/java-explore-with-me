package ru.practicum.ewm.events.service;

import ru.practicum.ewm.events.dto.*;

import java.util.List;

public interface EventService {
    EventFullDto createEvent(NewEventDto newEventDto, Long userId);

    List<EventShortDto> getShortEventListByUserId(Long userId, Integer from, Integer size);

    EventFullDto getFullEventById(Long userId, Long eventId);

    EventFullDto updateEventById(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventFullDto> getListEventByParams(ParamsSortDto paramsSortDto);

    EventFullDto confirmEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
