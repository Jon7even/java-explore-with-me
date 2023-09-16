package ru.practicum.ewm.events.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.CategoryEntity;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.mapper.LocationMapper;
import ru.practicum.ewm.events.model.EventEntity;
import ru.practicum.ewm.events.model.EventSort;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.model.LocationEntity;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.events.repository.LocationRepository;
import ru.practicum.ewm.events.utils.ValidatorDefaultFields;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.exception.IncorrectMadeRequestException;
import ru.practicum.ewm.exception.IntegrityConstraintException;
import ru.practicum.ewm.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.requests.mapper.RequestMapper;
import ru.practicum.ewm.requests.model.RequestEntity;
import ru.practicum.ewm.requests.model.RequestStatus;
import ru.practicum.ewm.requests.reopository.RequestRepository;
import ru.practicum.ewm.stats.client.StatClient;
import ru.practicum.ewm.stats.dto.HitCreateTO;
import ru.practicum.ewm.stats.dto.HitResponseTO;
import ru.practicum.ewm.stats.dto.RequestStatListTO;
import ru.practicum.ewm.users.dto.UserShortDto;
import ru.practicum.ewm.users.mapper.UserMapper;
import ru.practicum.ewm.users.model.UserEntity;
import ru.practicum.ewm.users.repository.UserRepository;
import ru.practicum.ewm.utils.ConverterPage;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.ewm.config.CommonConfig.*;
import static ru.practicum.ewm.constants.CommonSort.DEFAULT_SORT_BY_ID;
import static ru.practicum.ewm.constants.NamesExceptions.*;
import static ru.practicum.ewm.constants.NamesLogsInService.*;
import static ru.practicum.ewm.events.model.EventState.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatClient statClient;

    @Override
    public List<EventShortDto> getShortEventListByUserId(Long userId, Integer from, Integer size) {
        Pageable pageable = ConverterPage.getPageRequest(from, size, Optional.of(DEFAULT_SORT_BY_ID));

        log.debug("Get list events by [userId={}] and pages {}", userId, SERVICE_IN_DB);
        List<EventEntity> listEvents = eventRepository.findAllByInitiatorId(userId, pageable);

        if (listEvents.isEmpty()) {
            log.debug("Has returned empty list events {}", SERVICE_FROM_DB);
        } else {
            log.debug("Found list events [count={}] {}", listEvents.size(), SERVICE_FROM_DB);
        }

        return listEvents.stream()
                .map((eventEntity -> EventMapper.INSTANCE.toDTOShortResponseFromEntity(eventEntity,
                        CategoryMapper.INSTANCE.toDTOResponseFromEntity(eventEntity.getCategory()),
                        UserMapper.INSTANCE.toDTOShortResponseFromEntity(eventEntity.getInitiator())
                )))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto createEvent(NewEventDto newEventDto, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        log.debug("New newEventDto came [now={}] {} [newEventDto={}]", now, SERVICE_FROM_CONTROLLER, newEventDto);

        UserEntity userFromDB = findUserEntityById(userId);
        CategoryEntity categoryFromDB = findCategoryEntityById(newEventDto.getCategory());
        LocationEntity createdLocationFromDb = createLocation(newEventDto.getLocation());

        NewEventDto validatedEventDto = validateDefaultFields(newEventDto);
        EventEntity event = EventMapper.INSTANCE.toEntityFromDTOCreate(
                validatedEventDto, userFromDB, DEFAULT_INITIAL_STATE, categoryFromDB, now, createdLocationFromDb
        );

        log.debug("Add new entity [event={}] {}", event, SERVICE_IN_DB);
        EventEntity createdEvent = eventRepository.save(event);

        UserShortDto userShortDto = UserMapper.INSTANCE.toDTOShortResponseFromEntity(createdEvent.getInitiator());
        CategoryDto categoryDto = CategoryMapper.INSTANCE.toDTOResponseFromEntity(createdEvent.getCategory());
        Location location = LocationMapper.INSTANCE.toDTOResponseFromEntity(createdEvent.getLocation());

        log.debug("New event has returned [event={}] {}", event, SERVICE_FROM_DB);
        return EventMapper.INSTANCE.toDTOFullResponseFromEntity(createdEvent, categoryDto, userShortDto, location);
    }

    @Override
    public EventFullDto getFullEventById(Long userId, Long eventId) {
        UserEntity userFromDB = findUserEntityById(userId);

        log.debug("Get  event by [eventId={}] by [userId={}] {}", eventId, userId, SERVICE_IN_DB);
        Optional<EventEntity> foundEventEntity = eventRepository.findEventByIdAndInitiator(eventId, userFromDB);

        if (foundEventEntity.isPresent()) {
            EventEntity event = foundEventEntity.get();
            log.debug("Found [event={}] {}", event, SERVICE_FROM_DB);

            return EventMapper.INSTANCE.toDTOFullResponseFromEntity(event,
                    CategoryMapper.INSTANCE.toDTOResponseFromEntity(event.getCategory()),
                    UserMapper.INSTANCE.toDTOShortResponseFromEntity(event.getInitiator()),
                    LocationMapper.INSTANCE.toDTOResponseFromEntity(event.getLocation()));
        } else {
            log.warn("Event by [eventId={}] was not found", eventId);
            throw new EntityNotFoundException(String.format("Event with id=%d was not found", eventId));
        }
    }

    @Transactional
    @Override
    public EventFullDto updateEventById(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        log.debug("Event for update came {} [updateEventUserRequest={}]",
                SERVICE_FROM_CONTROLLER, updateEventUserRequest);
        UserEntity userFromDB = findUserEntityById(userId);

        Optional<EventEntity> foundEventEntity = eventRepository.findEventByIdAndInitiator(eventId, userFromDB);

        if (foundEventEntity.isPresent()) {
            EventEntity entityFromDb = foundEventEntity.get();
            log.debug("Found [event={}] {}", entityFromDb, SERVICE_FROM_DB);

            checkEventStateForUpdate(entityFromDb.getState());
            setValidFieldsForUpdate(entityFromDb, updateEventUserRequest);

            EventMapper.INSTANCE.updateEntityFromDTO(updateEventUserRequest, entityFromDb);

            log.debug("Updated [updatedEvent={}] {}", entityFromDb, SERVICE_IN_DB);
            EventEntity updatedEvent = eventRepository.save(entityFromDb);

            log.debug("Updated Event has returned [event={}] {}", updatedEvent, SERVICE_FROM_DB);
            return EventMapper.INSTANCE.toDTOFullResponseFromEntity(updatedEvent,
                    CategoryMapper.INSTANCE.toDTOResponseFromEntity(updatedEvent.getCategory()),
                    UserMapper.INSTANCE.toDTOShortResponseFromEntity(updatedEvent.getInitiator()),
                    LocationMapper.INSTANCE.toDTOResponseFromEntity(updatedEvent.getLocation()));
        } else {
            log.warn("Event by [eventId={}] was not found", eventId);
            throw new EntityNotFoundException(String.format("Event with id=%d was not found", eventId));
        }
    }

    @Override
    public List<EventFullDto> getListEventByParams(ParamsSortDto paramsSortDto) {
        Pageable pageable = ConverterPage.getPageRequest(paramsSortDto.getFrom(),
                paramsSortDto.getSize(), Optional.empty());
        log.debug("Params Sort came by admin [params={}] and pages {}", paramsSortDto, SERVICE_FROM_CONTROLLER);

        LocalDateTime rangeStart;
        LocalDateTime rangeEnd;
        if (paramsSortDto.getRangeStart() == null || paramsSortDto.getRangeEnd() == null) {
            LocalDateTime now = LocalDateTime.now();
            rangeStart = now;
            rangeEnd = now.plusMonths(DEFAULT_MONTHS_COUNT);
        } else {
            checkValidTimeRange(paramsSortDto.getRangeStart(), paramsSortDto.getRangeEnd());
            rangeStart = paramsSortDto.getRangeStart();
            rangeEnd = paramsSortDto.getRangeEnd();
        }

        log.debug("Get list by [rangeStart={}] and [rangeEnd={}] {}", rangeStart, rangeEnd, SERVICE_IN_DB);
        List<EventEntity> listEvents = eventRepository.findByAdminParamsAndPageable(
                paramsSortDto.getUsers(), paramsSortDto.getStates(), paramsSortDto.getCategories(),
                rangeStart, rangeEnd, pageable
        );

        if (listEvents.isEmpty()) {
            log.debug("Has returned empty list events {}", SERVICE_FROM_DB);
        } else {
            log.debug("Found list events [count={}] {}", listEvents.size(), SERVICE_FROM_DB);
        }

        return listEvents.stream()
                .map((eventEntity -> EventMapper.INSTANCE.toDTOFullResponseFromEntity(eventEntity,
                        CategoryMapper.INSTANCE.toDTOResponseFromEntity(eventEntity.getCategory()),
                        UserMapper.INSTANCE.toDTOShortResponseFromEntity(eventEntity.getInitiator()),
                        LocationMapper.INSTANCE.toDTOResponseFromEntity(eventEntity.getLocation()))
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto confirmEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.debug("Event for update came {} [updateEventAdminRequest={}]",
                SERVICE_FROM_CONTROLLER, updateEventAdminRequest);
        ValidatorDefaultFields.checkEventDateForAdmin(updateEventAdminRequest.getEventDate());

        Optional<EventEntity> foundEventEntity = eventRepository.findById(eventId);

        if (foundEventEntity.isPresent()) {
            EventEntity entityFromDb = foundEventEntity.get();
            log.debug("Found [event={}] {}", entityFromDb, SERVICE_FROM_DB);

            checkEventStateForConfirm(entityFromDb.getState(), updateEventAdminRequest.getStateAction());
            setValidFieldsForConfirm(entityFromDb, updateEventAdminRequest);

            LocalDateTime now = null;

            if (updateEventAdminRequest.getStateAction() != null) {
                if (entityFromDb.getState().equals(PUBLISHED)) {
                    now = LocalDateTime.now();
                }
            }
            EventMapper.INSTANCE.updateEntityFromDTO(updateEventAdminRequest, entityFromDb, now);

            log.debug("Updated [updatedEvent={}] {} by admin", entityFromDb, SERVICE_IN_DB);
            EventEntity updatedEvent = eventRepository.save(entityFromDb);

            log.debug("Updated Event has returned [event={}] {}", updatedEvent, SERVICE_FROM_DB);
            return EventMapper.INSTANCE.toDTOFullResponseFromEntity(updatedEvent,
                    CategoryMapper.INSTANCE.toDTOResponseFromEntity(updatedEvent.getCategory()),
                    UserMapper.INSTANCE.toDTOShortResponseFromEntity(updatedEvent.getInitiator()),
                    LocationMapper.INSTANCE.toDTOResponseFromEntity(updatedEvent.getLocation()));
        } else {
            log.warn("Event by [eventId={}] was not found", eventId);
            throw new EntityNotFoundException(String.format("Event with id=%d was not found", eventId));
        }
    }

    @Override
    public List<EventShortDto> getPublicListShortEventByParams(PublicParamsSortDto paramsSortDto) {
        Pageable pageable = ConverterPage.getPageRequest(paramsSortDto.getFrom(),
                paramsSortDto.getSize(), Optional.of(getValidSorting(paramsSortDto.getSort())));
        log.debug("Params Sort came by admin [params={}] and pages {}", paramsSortDto, SERVICE_FROM_CONTROLLER);

        LocalDateTime rangeStart;
        LocalDateTime rangeEnd;
        if (paramsSortDto.getRangeStart() == null || paramsSortDto.getRangeEnd() == null) {
            LocalDateTime now = LocalDateTime.now();
            rangeStart = now;
            rangeEnd = now.plusMonths(DEFAULT_MONTHS_COUNT);
        } else {
            checkValidTimeRange(paramsSortDto.getRangeStart(), paramsSortDto.getRangeEnd());
            rangeStart = paramsSortDto.getRangeStart();
            rangeEnd = paramsSortDto.getRangeEnd();
        }

        log.debug("Get Public list by [rangeStart={}] and [rangeEnd={}] {}", rangeStart, rangeEnd, SERVICE_IN_DB);
        List<EventEntity> listEvents;

        if (paramsSortDto.getOnlyAvailable()) {
            listEvents = eventRepository.findEventsOnlyAvailableByParamsAndPageable(
                    PUBLISHED, paramsSortDto.getText(), paramsSortDto.getCategories(),
                    paramsSortDto.getPaid(), rangeStart, rangeEnd, pageable
            );
        } else {
            listEvents = eventRepository.findEventsByParamsAndPageable(
                    PUBLISHED, paramsSortDto.getText(), paramsSortDto.getCategories(),
                    paramsSortDto.getPaid(), rangeStart, rangeEnd, pageable
            );
        }

        if (listEvents.isEmpty()) {
            log.debug("Has returned empty list events {}", SERVICE_FROM_DB);
        } else {
            log.debug("Found list events [count={}] {}", listEvents.size(), SERVICE_FROM_DB);
        }

        sendHitToStatsServer(paramsSortDto.getRequest());

        return listEvents.stream()
                .map((eventEntity -> EventMapper.INSTANCE.toDTOShortResponseFromEntity(eventEntity,
                        CategoryMapper.INSTANCE.toDTOResponseFromEntity(eventEntity.getCategory()),
                        UserMapper.INSTANCE.toDTOShortResponseFromEntity(eventEntity.getInitiator())
                )))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getPublicEventById(Long eventId, HttpServletRequest request) {
        log.debug("Get public event by [eventId={}] {}", eventId, SERVICE_IN_DB);
        Optional<EventEntity> foundEventEntity = eventRepository.findEventByIdAndState(eventId, PUBLISHED);

        if (foundEventEntity.isPresent()) {
            EventEntity event = foundEventEntity.get();
            log.debug("Found [event={}] {}", event, SERVICE_FROM_DB);

            event.setViews(Math.toIntExact(getHitsFromStatsServer(request)));
            if (event.getViews() != 0) {
                saveHits(event);
            }
            sendHitToStatsServer(request);

            return EventMapper.INSTANCE.toDTOFullResponseFromEntity(event,
                    CategoryMapper.INSTANCE.toDTOResponseFromEntity(event.getCategory()),
                    UserMapper.INSTANCE.toDTOShortResponseFromEntity(event.getInitiator()),
                    LocationMapper.INSTANCE.toDTOResponseFromEntity(event.getLocation()));
        } else {
            log.warn("Event by [eventId={}] with state Published was not found", eventId);
            throw new EntityNotFoundException(String.format("Event with id=%d was not found", eventId));
        }
    }

    @Override
    public List<ParticipationRequestDto> getAllRequestsByInitiator(Long userId, Long eventId) {
        UserEntity userFromDB = findUserEntityById(userId);

        log.debug("Get all requests for event by [eventId={}] for user [userId={}] {}", eventId, userId, SERVICE_IN_DB);
        List<RequestEntity> listRequests = requestRepository.findAllByEventIdAndEventInitiator(eventId, userFromDB);

        if (listRequests.isEmpty()) {
            log.debug("Has returned empty list requests {}", SERVICE_FROM_DB);
        } else {
            log.debug("Found list requests [count={}] {}", listRequests.size(), SERVICE_FROM_DB);
        }

        return RequestMapper.INSTANCE.toDTOResponseFromEntityList(listRequests);
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult confirmRequestByInitiator(Long userId, Long eventId,
                                                                    EventRequestStatusUpdateRequest updateRequest) {
        log.debug("UpdateRequestDto for confirm request came {} [updateRequest={}]",
                SERVICE_FROM_CONTROLLER, updateRequest);

        EventEntity eventFromDb = findEventEntityById(eventId);
        if (eventFromDb.getParticipantLimit() == 0 || !eventFromDb.getRequestModeration()) {
            throw new IntegrityConstraintException(CONFIRM_NOT_REQUIRED);
        }

        UserEntity userFromDB = findUserEntityById(userId);
        if (!eventFromDb.getInitiator().getId().equals(userFromDB.getId())) {
            throw new IntegrityConstraintException(USER_NOT_INITIATOR);
        }

        List<RequestEntity> requestsFromDb = requestRepository.findAllByIdInAndEventInitiatorAndEvent(
                updateRequest.getRequestIds(), userFromDB, eventFromDb);
        log.debug("Has returned list [requests={}] {}", requestsFromDb, SERVICE_FROM_DB);
        EventRequestStatusUpdateResult eventRequestUpdatedStatus;

        switch (updateRequest.getStatus()) {

            case CONFIRMED:
                int countAvailableSeats = eventFromDb.getParticipantLimit() - eventFromDb.getConfirmedRequests();
                int countConfirmRequest = updateRequest.getRequestIds().size();
                log.debug("countAvailableSeats = {}, countConfirmRequest={}", countAvailableSeats, countConfirmRequest);
                if ((countAvailableSeats - countConfirmRequest) < 0) {
                    throw new IntegrityConstraintException(EVENT_IS_FULL_COUNT + countAvailableSeats);
                }

                requestsFromDb.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
                eventFromDb.setConfirmedRequests(
                        eventFromDb.getConfirmedRequests() + countConfirmRequest
                );

                log.debug("Confirm requests, set status [updateRequest={}] in [EventsIds={}] {}",
                        updateRequest.getStatus(), updateRequest.getRequestIds(), SERVICE_IN_DB);
                List<RequestEntity> requestsConfirm = requestRepository.saveAll(requestsFromDb);
                eventRequestUpdatedStatus = RequestMapper.INSTANCE.toDTOResponseFromDTOList(
                        RequestMapper.INSTANCE.toDTOResponseFromEntityList(requestsConfirm),
                        Collections.emptyList()
                );

                log.debug("Update ConfirmedRequests in [event={}] {}", eventFromDb, SERVICE_IN_DB);
                eventRepository.save(eventFromDb);
                break;

            case REJECTED:
                if (checkStatusInListRequests(requestsFromDb, RequestStatus.CONFIRMED)) {
                    throw new IntegrityConstraintException(REQUESTS_ALREADY_CONFIRMED);
                }
                requestsFromDb.forEach(request -> request.setStatus(RequestStatus.REJECTED));

                log.debug("Confirm requests, set status [updateRequest={}] in [eventsIds={}] {}",
                        updateRequest.getStatus(), updateRequest.getRequestIds(), SERVICE_IN_DB);
                List<RequestEntity> requestsReject = requestRepository.saveAll(requestsFromDb);
                eventRequestUpdatedStatus = RequestMapper.INSTANCE.toDTOResponseFromDTOList(
                        Collections.emptyList(),
                        RequestMapper.INSTANCE.toDTOResponseFromEntityList(requestsReject)
                );
                break;

            default:
                throw new IntegrityConstraintException(INCORRECT_STATUS);
        }

        return eventRequestUpdatedStatus;
    }

    private LocationEntity createLocation(Location location) {
        log.debug("New LocationDto came from service [location={}]", location);
        LocationEntity locationForSaved = LocationMapper.INSTANCE.toEntityFromDTOCreate(location);

        log.debug("Add new entity [location={}] {}", locationForSaved, SERVICE_IN_DB);
        LocationEntity createdLocation = locationRepository.save(locationForSaved);

        log.debug("New location has returned [location={}] {}", createdLocation, SERVICE_FROM_DB);
        return createdLocation;
    }

    private LocationEntity updateLocation(Location location, Long id) {
        log.debug("New LocationDto came from service [location={}]", location);
        LocationEntity locationForUpdated = LocationMapper.INSTANCE.toEntityFromDTOUpdate(location, id);

        log.debug("Update entity [location={}] {}", locationForUpdated, SERVICE_IN_DB);
        LocationEntity updatedLocation = locationRepository.save(locationForUpdated);

        log.debug("Location has returned [location={}] {}", updatedLocation, SERVICE_FROM_DB);
        return updatedLocation;
    }

    private CategoryEntity findCategoryEntityById(Integer idCategory) {
        log.debug("Get category entity for checking by [idCategory={}] {}", idCategory, SERVICE_IN_DB);
        Optional<CategoryEntity> foundCheckCategory = categoryRepository.findById(idCategory);

        if (foundCheckCategory.isPresent()) {
            log.debug("Check was successful found [category={}] {}", foundCheckCategory.get(), SERVICE_FROM_DB);
            return foundCheckCategory.get();
        } else {
            log.warn("Category by [id={}] was not found", idCategory);
            throw new EntityNotFoundException(String.format("Category with id=%d was not found", idCategory));
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

    private NewEventDto validateDefaultFields(NewEventDto newEventDto) {
        newEventDto.setParticipantLimit(ValidatorDefaultFields.limitParticipant(newEventDto.getParticipantLimit()));
        newEventDto.setPaid(ValidatorDefaultFields.paid(newEventDto.getPaid()));
        newEventDto.setRequestModeration(ValidatorDefaultFields.requestModeration(newEventDto.getRequestModeration()));
        return newEventDto;
    }

    private void checkEventStateForUpdate(EventState eventState) {
        if (eventState.equals(PENDING) || eventState.equals(CANCELED)) {
            log.debug("Check EventState [state={}] successful", eventState);
        } else {
            throw new IntegrityConstraintException(EVENT_UPDATE_REJECTED);
        }
    }

    private void checkEventStateForConfirm(EventState stateInDb, EventState stateDto) {
        if (stateDto != null) {
            if (stateInDb.equals(PUBLISHED) && stateDto.equals(REJECT_EVENT)) {
                throw new IntegrityConstraintException(EVENT_PUBLISHED_REJECTED);
            }
        }

        if (stateInDb.equals(PENDING)) {
            log.debug("Check EventState [state={}] successful", stateInDb);
        } else {
            throw new IntegrityConstraintException(EVENT_CONFIRM_REJECTED);
        }
    }

    private EventState getValidEventStateForUpdate(EventState eventState) {
        EventState stateForUpdate;

        switch (eventState) {
            case SEND_TO_REVIEW:
                stateForUpdate = PENDING;
                break;
            case CANCEL_REVIEW:
                stateForUpdate = CANCELED;
                break;
            default:
                throw new IntegrityConstraintException(EVENT_UPDATE_REJECTED);
        }
        return stateForUpdate;
    }

    private Sort getValidSorting(EventSort sort) {
        Sort sorting;
        if (sort == EventSort.EVENT_DATE) {
            sorting = Sort.by(Sort.Direction.DESC, "eventDate");
        } else {
            sorting = Sort.by(Sort.Direction.DESC, "views");
        }
        return sorting;
    }

    private EventState getValidEventStateForConfirm(EventState eventState) {
        EventState stateForConfirm;

        switch (eventState) {
            case PUBLISH_EVENT:
                stateForConfirm = PUBLISHED;
                break;
            case REJECT_EVENT:
                stateForConfirm = CANCELED;
                break;
            default:
                throw new IntegrityConstraintException(EVENT_CONFIRM_REJECTED);
        }
        return stateForConfirm;
    }

    private void setValidFieldsForUpdate(EventEntity entityFromDb, UpdateEventUserRequest updateEventUserRequest) {
        if (updateEventUserRequest.getCategory() != null) {
            CategoryEntity foundCategoryFromRepository = findCategoryEntityById(updateEventUserRequest.getCategory());
            if (!foundCategoryFromRepository.equals(entityFromDb.getCategory())) {
                entityFromDb.setCategory(foundCategoryFromRepository);
            }
        }

        if (updateEventUserRequest.getStateAction() != null) {
            entityFromDb.setState(getValidEventStateForUpdate(updateEventUserRequest.getStateAction()));
        }

        if (updateEventUserRequest.getLocation() != null) {
            if (!entityFromDb.getLocation().getLat().equals(updateEventUserRequest.getLocation().getLat()) ||
                    !entityFromDb.getLocation().getLon().equals(updateEventUserRequest.getLocation().getLon())) {
                entityFromDb.setLocation(updateLocation(updateEventUserRequest.getLocation(), entityFromDb.getId()));
            }
        }
    }

    private void setValidFieldsForConfirm(EventEntity entityFromDb, UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.getCategory() != null) {
            CategoryEntity foundCategoryFromRepository = findCategoryEntityById(updateEventAdminRequest.getCategory());
            if (!foundCategoryFromRepository.equals(entityFromDb.getCategory())) {
                entityFromDb.setCategory(foundCategoryFromRepository);
            }
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            entityFromDb.setState(getValidEventStateForConfirm(updateEventAdminRequest.getStateAction()));
        }

        if (updateEventAdminRequest.getLocation() != null) {
            if (!entityFromDb.getLocation().getLat().equals(updateEventAdminRequest.getLocation().getLat()) ||
                    !entityFromDb.getLocation().getLon().equals(updateEventAdminRequest.getLocation().getLon())) {
                entityFromDb.setLocation(updateLocation(updateEventAdminRequest.getLocation(), entityFromDb.getId()));
            }
        }
    }

    private void checkValidTimeRange(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IncorrectMadeRequestException(START_AFTER_END);
        }
    }

    private void saveHits(EventEntity event) {
        log.debug("Set [eventId={}] [hits={}] {}", event.getId(), event.getViews(), SERVICE_IN_DB);
        eventRepository.save(event);
    }

    private Long getHitsFromStatsServer(HttpServletRequest request) {
        ResponseEntity<Object> responseFromServer = statClient.getStats(RequestStatListTO.builder()
                .start(DATA_SEARCH_HITS)
                .end(LocalDateTime.now())
                .uris(Collections.singletonList(request.getRequestURI()))
                .unique(true)
                .build());
        if (responseFromServer.getStatusCode().is2xxSuccessful()) {
            log.debug("Get hits successfully [response={}]", responseFromServer);
            ObjectMapper mapper = new ObjectMapper();

            List<HitResponseTO> listResponse = Collections.emptyList();
            Long hits;

            try {
                listResponse = Collections.singletonList(mapper.readValue(
                        mapper.writeValueAsString(responseFromServer.getBody()), HitResponseTO.class));
            } catch (JsonProcessingException e) {
                log.error("Something went wrong on the statistics server, contact Dev-Ops. Error: Json");
            }

            if (listResponse.isEmpty()) {
                log.debug("HitResponseTO - empty, given hits default 0");
                hits = 0L;
            } else if (listResponse.size() == 1) {

                if (listResponse.get(0).getUri().equals(request.getRequestURI())) {
                    hits = listResponse.get(0).getHits();
                } else {
                    log.error("Something went wrong on the statistics server, contact Dev-Ops. Error: Uri");
                    hits = 0L;
                }

            } else {
                log.error("Something went wrong on the statistics server, contact Dev-Ops. Error: Duplicate response");
                hits = 0L;
            }

            log.debug("Return [hits={}]", hits);
            return hits;
        } else {
            log.error("Something went wrong on the statistics server, contact Dev-Ops");
            return 0L;
        }
    }

    private void sendHitToStatsServer(HttpServletRequest request) {
        ResponseEntity<Object> responseFromServer = statClient.createHit(HitCreateTO.builder()
                .app("Explore With Me ***Stats Main Server***")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
        if (responseFromServer.getStatusCode().is2xxSuccessful()) {
            log.debug("View saved successfully [request={}]", request);
        } else {
            log.error("Something went wrong on the statistics server, contact Dev-Ops");
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

    private Boolean checkStatusInListRequests(List<RequestEntity> requestEntities, RequestStatus status) {
        return requestEntities.stream().anyMatch(request -> request.getStatus().equals(status));
    }

}
