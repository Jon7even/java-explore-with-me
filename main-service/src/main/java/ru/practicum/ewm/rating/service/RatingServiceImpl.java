package ru.practicum.ewm.rating.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.EventEntity;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.rating.model.RatingId;
import ru.practicum.ewm.rating.repository.*;
import ru.practicum.ewm.exception.EntityNotDeletedException;
import ru.practicum.ewm.exception.IntegrityConstraintException;
import ru.practicum.ewm.rating.mapper.RatingMapper;
import ru.practicum.ewm.rating.model.RatingEntity;
import ru.practicum.ewm.rating.model.RatingSort;
import ru.practicum.ewm.users.mapper.UserMapper;
import ru.practicum.ewm.users.model.UserEntity;
import ru.practicum.ewm.users.repository.UserRepository;
import ru.practicum.ewm.utils.ConverterPage;
import ru.practicum.ewm.utils.EventTopRatingComparator;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.ewm.constants.NamesExceptions.*;
import static ru.practicum.ewm.constants.NamesLogsInService.SERVICE_FROM_DB;
import static ru.practicum.ewm.constants.NamesLogsInService.SERVICE_IN_DB;
import static ru.practicum.ewm.events.model.EventState.PUBLISHED;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public void addLikeByEventId(Long userId, Long eventId, Boolean isPositive) {
        UserEntity userFromDB = findUserEntityById(userId);
        EventEntity eventFromDb = findEventEntityById(eventId);
        RatingEntity ratingForSave = setValidFieldsForLike(userFromDB, eventFromDb, isPositive);

        log.debug("Add entity [rating={}] {}", ratingForSave, SERVICE_IN_DB);
        ratingRepository.save(ratingForSave);
    }

    @Transactional
    @Override
    public void removeLikeByEventId(Long userId, Long eventId) {
        UserEntity userFromDB = findUserEntityById(userId);
        EventEntity eventFromDb = findEventEntityById(eventId);

        log.debug("Remove like by [userId={}] and [eventId={}] {}", userId, eventId, SERVICE_IN_DB);
        ratingRepository.deleteByLikerAndEvent(userFromDB, eventFromDb);
        boolean isRemoved = ratingRepository.existsByLikerAndEvent(userFromDB, eventFromDb);

        if (!isRemoved) {
            log.debug("Like by [userId={}] has removed {}", userId, SERVICE_FROM_DB);
        } else {
            log.error("Like by [userId={}] was not removed", userId);
            throw new EntityNotDeletedException(String.format("Like by user id=%d was not deleted", userId));
        }
    }

    @Override
    public List<EventShortDto> getTopEventsBySortAndPages(RatingSort sort, Integer from, Integer size) {
        Pageable pageable = ConverterPage.getPageRequest(from, size, Optional.empty());

        log.debug("Get TOP public list events by [sort={}] and pages {}", sort, SERVICE_IN_DB);
        List<EventEntity> listEvents;

        switch (sort) {
            case LIKES:
                List<EventEntity> listFromDBLikes = eventRepository.findEventsByTopLikes(pageable);
                listEvents = listFromDBLikes.stream().sorted(Comparator.comparing((EventEntity e) ->
                        e.getLikes().size()).reversed()).collect(Collectors.toList());
                break;
            case DISLIKES:
                List<EventEntity> listFromDBDisLikes = eventRepository.findEventsByTopDisLikes(pageable);
                listEvents = listFromDBDisLikes.stream().sorted(Comparator.comparing((EventEntity e) ->
                        e.getDisLikes().size()).reversed()).collect(Collectors.toList());
                break;
            case TOTAL_RATING:
                List<EventEntity> listFromDBTop = eventRepository.findEventsByTotalRating(pageable);
                listEvents = listFromDBTop.stream().sorted(new EventTopRatingComparator().reversed())
                        .collect(Collectors.toList());
                break;
            default:
                throw new IntegrityConstraintException(UNSUPPORTED_SORT);
        }

        if (listEvents.isEmpty()) {
            log.debug("Has returned empty list events {}", SERVICE_FROM_DB);
        } else {
            log.debug("Found list events [count={}] {}", listEvents.size(), SERVICE_FROM_DB);
        }

        return listEvents.stream()
                .map((eventEntity -> EventMapper.INSTANCE.toDTOShortResponseFromEntity(eventEntity,
                        CategoryMapper.INSTANCE.toDTOResponseFromEntity(eventEntity.getCategory()),
                        UserMapper.INSTANCE.toDTOShortResponseFromEntity(eventEntity.getInitiator()),
                        RatingMapper.INSTANCE.toListDTOResponseFromListEntity(eventEntity.getLikes()),
                        RatingMapper.INSTANCE.toListDTOResponseFromListEntity(eventEntity.getDisLikes())
                )))
                .collect(Collectors.toList());
    }

    private RatingEntity setValidFieldsForLike(UserEntity liker, EventEntity event, Boolean isPositive) {
        if (event.getInitiator().equals(liker)) {
            throw new IntegrityConstraintException(EVENT_IS_YOUR);
        }

        if (!event.getState().equals(PUBLISHED)) {
            throw new IntegrityConstraintException(EVENT_NOT_PUBLISHED);
        }

        LocalDateTime dateTime = LocalDateTime.now();

        Optional<RatingEntity> ratingFromDB = ratingRepository.findByLikerAndEvent(liker, event);

        if (ratingFromDB.isPresent()) {
            RatingEntity ratingFromSave = ratingFromDB.get();

            log.debug("Like is already in DB. [isPositiveFromDB={}] [isPositiveFromController={}]",
                    ratingFromSave.getIsPositive(), isPositive);
            ratingFromSave.setUpdatedOn(dateTime);
            ratingFromSave.setIsPositive(isPositive);
            log.debug("Performing an update entity rating...");

            return ratingFromSave;
        } else {
            log.debug("Creating a new entity rating...");
            return RatingEntity.builder()
                    .id(RatingId.builder().likerId(liker.getId()).eventId(event.getId()).build())
                    .liker(liker)
                    .event(event)
                    .isPositive(isPositive)
                    .createdOn(dateTime)
                    .build();
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

}
