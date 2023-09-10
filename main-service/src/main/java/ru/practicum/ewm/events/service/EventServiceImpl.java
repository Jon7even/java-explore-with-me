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
import static ru.practicum.ewm.constants.CommonSort.DEFAULT_SORT_BY_ID;
import static ru.practicum.ewm.constants.NamesExceptions.EVENT_UPDATE_REJECTED;
import static ru.practicum.ewm.constants.NamesLogsInService.*;
import static ru.practicum.ewm.events.model.EventState.CANCELED;
import static ru.practicum.ewm.events.model.EventState.PENDING;

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
        log.debug("Event for update came {} [eventDto={}]", SERVICE_FROM_CONTROLLER, updateEventUserRequest);
        UserEntity userFromDB = findUserEntityById(userId);

        Optional<EventEntity> foundEventEntity = eventRepository.findEventByIdAndInitiator(eventId, userFromDB);

        if (foundEventEntity.isPresent()) {
            EventEntity entityFromDb = foundEventEntity.get();
            log.debug("Found [event={}] {}", entityFromDb, SERVICE_FROM_DB);

            checkEventStateForUpdate(entityFromDb.getState());
            setValidFields(entityFromDb, updateEventUserRequest);

            EventMapper.INSTANCE.updateEntityFromDTO(updateEventUserRequest, entityFromDb);

            log.debug("Updated [oldEvent={}] on [updatedEvent={}] {}", foundEventEntity.get(), entityFromDb, SERVICE_IN_DB);
            EventEntity createdEvent = eventRepository.save(entityFromDb);

            log.debug("Updated Event has returned [event={}] {}", createdEvent, SERVICE_FROM_DB);
            return EventMapper.INSTANCE.toDTOFullResponseFromEntity(createdEvent,
                    CategoryMapper.INSTANCE.toDTOResponseFromEntity(createdEvent.getCategory()),
                    UserMapper.INSTANCE.toDTOShortResponseFromEntity(createdEvent.getInitiator()),
                    LocationMapper.INSTANCE.toDTOResponseFromEntity(createdEvent.getLocation()));
        } else {
            log.warn("Event by [eventId={}] was not found", eventId);
            throw new EntityNotFoundException(String.format("Event with id=%d was not found", eventId));
        }
    }

    @Override
    List<EventFullDto> getListEventByParams(ParamsSortDto paramsSortDto) {
        //toDO
    }

    @Transactional
    @Override
    public EventFullDto confirmEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        //toDO
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
        if (eventState.equals(EventState.PENDING) || eventState.equals(EventState.CANCELED)) {
            log.debug("Check EventState [state={}] successful", eventState);
        } else {
            throw new IntegrityConstraintException(EVENT_UPDATE_REJECTED);
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

    private void setValidFields(EventEntity entityFromDb, UpdateEventUserRequest updateEventUserRequest) {
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

}
