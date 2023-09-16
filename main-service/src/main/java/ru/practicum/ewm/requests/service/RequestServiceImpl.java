package ru.practicum.ewm.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.events.model.EventEntity;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.exception.IntegrityConstraintException;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.requests.mapper.RequestMapper;
import ru.practicum.ewm.requests.model.RequestEntity;
import ru.practicum.ewm.requests.reopository.RequestRepository;
import ru.practicum.ewm.users.model.UserEntity;
import ru.practicum.ewm.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.practicum.ewm.constants.NamesExceptions.*;
import static ru.practicum.ewm.constants.NamesLogsInService.*;
import static ru.practicum.ewm.requests.model.RequestStatus.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getRequestsByUserId(Long userId) {
        UserEntity userFromDB = findUserEntityById(userId);

        log.debug("Get requests by [userId={}]{}", userId, SERVICE_IN_DB);
        List<RequestEntity> listRequests = requestRepository.findAllByRequester(userFromDB);

        if (listRequests.isEmpty()) {
            log.debug("Has returned empty list requests {}", SERVICE_FROM_DB);
        } else {
            log.debug("Found list requests [count={}] {}", listRequests.size(), SERVICE_FROM_DB);
        }

        return RequestMapper.INSTANCE.toDTOResponseFromEntityList(listRequests);
    }

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        log.debug("Came new request by [userId={}] for [eventId={}] {} ", userId, eventId, SERVICE_FROM_CONTROLLER);
        UserEntity userFromDB = findUserEntityById(userId);
        EventEntity eventFromDb = findEventEntityById(eventId);

        if (requestRepository.existsByRequesterAndEvent(userFromDB, eventFromDb)) {
            throw new IntegrityConstraintException(REQUEST_ALREADY_EXIST);
        }

        RequestEntity requestForCreate = setValidatedFieldsForNewRequest(userFromDB, eventFromDb);
        log.debug("Add new request [request={}] {}", requestForCreate, SERVICE_IN_DB);
        RequestEntity createdRequest = requestRepository.save(requestForCreate);

        if (createdRequest.getStatus().equals(CONFIRMED)) {
            int confirmedRequests = eventFromDb.getConfirmedRequests() + 1;
            eventFromDb.setConfirmedRequests(confirmedRequests);
            eventRepository.save(eventFromDb);
            log.debug("set requests [count={}] to [eventId={}] {}", confirmedRequests, eventId, SERVICE_IN_DB);
        }

        return RequestMapper.INSTANCE.toDTOResponseFromEntity(createdRequest);
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequestByUser(Long userId, Long requestId) {
        UserEntity userFromDB = findUserEntityById(userId);
        RequestEntity requestFromDb = findRequestEntityById(requestId);

        if (!requestFromDb.getRequester().equals(userFromDB)) {
            throw new EntityNotFoundException(String.format("Request with id=%d and requester id=%d was not found",
                    requestId, userId));
        }
        requestFromDb.setStatus(CANCELED);
        log.debug("Set CANCELED status [request={}] {}", requestFromDb, SERVICE_IN_DB);

        requestRepository.save(requestFromDb);
        return RequestMapper.INSTANCE.toDTOResponseFromEntity(requestFromDb);
    }

    private RequestEntity findRequestEntityById(Long requestId) {
        log.debug("Get request entity for checking by [requestId={}] {}", requestId, SERVICE_IN_DB);
        Optional<RequestEntity> foundCheckRequestEntity = requestRepository.findById(requestId);

        if (foundCheckRequestEntity.isPresent()) {
            log.debug("Check was successful found [request={}] {}", foundCheckRequestEntity.get(), SERVICE_FROM_DB);
            return foundCheckRequestEntity.get();
        } else {
            log.warn("Request by [requestId={}] was not found", requestId);
            throw new EntityNotFoundException(String.format("Request with id=%d was not found", requestId));
        }
    }

    private UserEntity findUserEntityById(Long userId) {
        log.debug("Get user entity for checking by [userId={}] {}", userId, SERVICE_IN_DB);
        Optional<UserEntity> foundCheckUser = userRepository.findById(userId);

        if (foundCheckUser.isPresent()) {
            log.debug("Check was successful found [user={}] {}", foundCheckUser.get(), SERVICE_FROM_DB);
            return foundCheckUser.get();
        } else {
            log.warn("User by [userId={}] was not found", userId);
            throw new EntityNotFoundException(String.format("User with id=%d was not found", userId));
        }
    }

    private EventEntity findEventEntityById(Long eventId) {
        log.debug("Get event entity for checking by [eventId={}] {}", eventId, SERVICE_IN_DB);
        Optional<EventEntity> foundCheckEvent = eventRepository.findById(eventId);

        if (foundCheckEvent.isPresent()) {
            log.debug("Check was successful found [event={}] {}", foundCheckEvent.get(), SERVICE_FROM_DB);
            return foundCheckEvent.get();
        } else {
            log.warn("Event by [eventId={}] was not found", foundCheckEvent);
            throw new EntityNotFoundException(String.format("Event with id=%d was not found", eventId));
        }
    }

    private RequestEntity setValidatedFieldsForNewRequest(UserEntity user, EventEntity event) {
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new IntegrityConstraintException(EVENT_NOT_PUBLISHED);
        }

        if (event.getInitiator().equals(user)) {
            throw new IntegrityConstraintException(REJECTED_REQUEST_INITIATOR);
        }

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= event.getConfirmedRequests()) {
            throw new IntegrityConstraintException(EVENT_IS_FULL);
        }

        LocalDateTime now = LocalDateTime.now();

        RequestEntity requestForCreate = RequestEntity.builder()
                .created(now)
                .requester(user)
                .event(event)
                .build();

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            requestForCreate.setStatus(CONFIRMED);
        } else {
            requestForCreate.setStatus(PENDING);
        }
        log.debug("Creating entity for save is successful [requestForCreate={}]", requestForCreate);

        return requestForCreate;
    }

}
