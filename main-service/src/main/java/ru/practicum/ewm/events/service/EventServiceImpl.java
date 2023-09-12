package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.model.LocationEntity;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.events.repository.LocationRepository;
import ru.practicum.ewm.events.utils.ValidatorDefaultFields;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.exception.IncorrectMadeRequestException;
import ru.practicum.ewm.exception.IntegrityConstraintException;
import ru.practicum.ewm.stats.client.StatClient;
import ru.practicum.ewm.users.dto.UserShortDto;
import ru.practicum.ewm.users.mapper.UserMapper;
import ru.practicum.ewm.users.model.UserEntity;
import ru.practicum.ewm.users.repository.UserRepository;
import ru.practicum.ewm.utils.ConverterPage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.ewm.config.CommonConfig.DEFAULT_INITIAL_STATE;
import static ru.practicum.ewm.config.CommonConfig.DEFAULT_MONTHS_COUNT;
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
        log.debug("Get  event by [eventId={}] by [userId={}] {}", eventId, userId, SERVICE_IN_DB);
        UserEntity userFromDB = findUserEntityById(userId);
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
                paramsSortDto.getSize(), Optional.of(DEFAULT_SORT_BY_ID));
        log.debug("Params Sort came by admin [params={}] and pages {}", paramsSortDto, SERVICE_FROM_CONTROLLER);

        LocalDateTime rangeStart;
        LocalDateTime rangeEnd;
        if (paramsSortDto.getRangeStart() == null || paramsSortDto.getRangeEnd() == null ||
                paramsSortDto.getRangeStart() == null && paramsSortDto.getRangeEnd() == null) {
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

}
