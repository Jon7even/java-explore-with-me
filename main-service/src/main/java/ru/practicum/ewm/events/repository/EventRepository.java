package ru.practicum.ewm.events.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.events.model.EventEntity;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.users.model.UserEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {
    List<EventEntity> findAllByInitiatorId(@Param("id") Long initiatorId, Pageable pageable);

    boolean existsEventByCategoryId(@Param("categoryId") Integer categoryId);

    Optional<EventEntity> findEventByIdAndInitiator(@Param("id") Long id, @Param("initiator") UserEntity initiator);

    Optional<EventEntity> findEventByIdAndState(@Param("id") Long id, @Param("state") EventState state);

    @Query("SELECT ev " +
            " FROM EventEntity AS ev " +
            " LEFT JOIN FETCH ev.initiator " +
            " LEFT JOIN FETCH ev.location " +
            " LEFT JOIN FETCH ev.category " +
            "WHERE ev.eventDate >= :rangeStart " +
            "  AND ev.eventDate <= :rangeEnd " +
            "  AND (COALESCE(:users, NULL)        IS NULL OR ev.initiator.id  IN (:users) ) " +
            "  AND (COALESCE(:states, NULL)       IS NULL OR ev.state         IN (:states) ) " +
            "  AND (COALESCE(:categories, NULL)   IS NULL OR ev.category.id   IN (:categories) )")
    List<EventEntity> findByAdminParamsAndPageable(@Param("users") List<Long> users,
                                                   @Param("states") List<EventState> states,
                                                   @Param("categories") List<Integer> categories,
                                                   @Param("rangeStart") LocalDateTime rangeStart,
                                                   @Param("rangeEnd") LocalDateTime rangeEnd,
                                                   Pageable pageable);

    @Query("SELECT ev " +
            " FROM EventEntity AS ev " +
            " LEFT JOIN FETCH ev.initiator " +
            " LEFT JOIN FETCH ev.location " +
            " LEFT JOIN FETCH ev.category " +
            "WHERE ev.eventDate >= :rangeStart " +
            "  AND ev.eventDate <= :rangeEnd " +
            "  AND ev.state      = :state " +
            "  AND (COALESCE(:paid, NULL)           IS NULL OR ev.paid = :paid ) " +
            "  AND (COALESCE(:categories, NULL)     IS NULL OR ev.category.id IN (:categories) ) " +
            "  AND (COALESCE(:text, NULL)           IS NULL OR " +
            "                      (LOWER (ev.annotation) LIKE LOWER(concat('%', :text, '%')) " +
            "                           OR LOWER (ev.description) LIKE LOWER(concat('%', :text, '%'))  ) )" +
            "  AND ev.participantLimit = 0 " +
            "      OR ev.participantLimit <= ev.confirmedRequests")
    List<EventEntity> findEventsOnlyAvailableByParamsAndPageable(@Param("state") EventState state,
                                                                 @Param("text") String text,
                                                                 @Param("categories") List<Integer> categories,
                                                                 @Param("paid") Boolean paid,
                                                                 @Param("rangeStart") LocalDateTime rangeStart,
                                                                 @Param("rangeEnd") LocalDateTime rangeEnd,
                                                                 Pageable pageable);

    @Query("SELECT ev " +
            " FROM EventEntity AS ev " +
            " LEFT JOIN FETCH ev.initiator " +
            " LEFT JOIN FETCH ev.location " +
            " LEFT JOIN FETCH ev.category " +
            "WHERE ev.eventDate >= :rangeStart " +
            "  AND ev.eventDate <= :rangeEnd " +
            "  AND ev.state      = :state " +
            "  AND (COALESCE(:paid, NULL)           IS NULL OR ev.paid = :paid ) " +
            "  AND (COALESCE(:categories, NULL)     IS NULL OR ev.category.id IN (:categories) ) " +
            "  AND (COALESCE(:text, NULL)           IS NULL OR " +
            "                      (LOWER (ev.annotation) LIKE LOWER(concat('%', :text, '%')) " +
            "                           OR LOWER (ev.description) LIKE LOWER(concat('%', :text, '%'))  ) )")
    List<EventEntity> findEventsByParamsAndPageable(@Param("state") EventState state,
                                                    @Param("text") String text,
                                                    @Param("categories") List<Integer> categories,
                                                    @Param("paid") Boolean paid,
                                                    @Param("rangeStart") LocalDateTime rangeStart,
                                                    @Param("rangeEnd") LocalDateTime rangeEnd,
                                                    Pageable pageable);
}
