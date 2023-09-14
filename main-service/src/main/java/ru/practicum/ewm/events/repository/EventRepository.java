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

    Optional<EventEntity> findEventByIdAndInitiator(@Param("id") Long id, @Param("initiator") UserEntity initiator);

    Optional<EventEntity> findEventByIdAndState(@Param("id") Long id, @Param("state") EventState state);

    @Query("SELECT ev " +
            " FROM EventEntity AS ev " +
            " LEFT JOIN FETCH ev.initiator " +
            " LEFT JOIN FETCH ev.location " +
            " LEFT JOIN FETCH ev.category " +
            "WHERE ev.eventDate >= :rangeStart " +
            "  AND ev.eventDate <= :rangeEnd " +
            "  AND ( (:users)      IS NULL OR ( (:users)      IS NOT NULL AND ev.initiator.id IN (:users) ) ) " +
            "  AND ( (:states)     IS NULL OR ( (:states)     IS NOT NULL AND ev.state IN (:states) ) ) " +
            "  AND ( (:categories) IS NULL OR ( (:categories) IS NOT NULL AND ev.category.id IN (:categories) ) ) ")
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
            "  AND ( :paid         IS NULL OR ( :paid         IS NOT NULL AND ev.paid = :paid ) ) " +
            "  AND ( (:categories) IS NULL OR ( (:categories) IS NOT NULL AND ev.category.id IN (:categories) ) ) " +
            "  AND ( :text         IS NULL OR ( :text         IS NOT NULL AND " +
            "                      LOWER (ev.annotation) LIKE LOWER(concat('%', :text, '%')) " +
            "                           OR LOWER (ev.description) LIKE LOWER(concat('%', :text, '%'))  ) )")
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
            "  AND ( :paid         IS NULL OR ( :paid         IS NOT NULL AND ev.paid = :paid ) ) " +
            "  AND ( (:categories) IS NULL OR ( (:categories) IS NOT NULL AND ev.category.id IN (:categories) ) ) " +
            "  AND ( :text         IS NULL OR ( :text         IS NOT NULL AND " +
            "                      LOWER (ev.annotation) LIKE LOWER(concat('%', :text, '%')) " +
            "                           OR LOWER (ev.description) LIKE LOWER(concat('%', :text, '%'))  ) ) " +
            "  AND ev.participantLimit = 0 " +
            "      OR ev.participantLimit <= ev.confirmedRequests")
    List<EventEntity> findEventsByParamsAndPageable(@Param("state") EventState state,
                                                    @Param("text") String text,
                                                    @Param("categories") List<Integer> categories,
                                                    @Param("paid") Boolean paid,
                                                    @Param("rangeStart") LocalDateTime rangeStart,
                                                    @Param("rangeEnd") LocalDateTime rangeEnd,
                                                    Pageable pageable);

}