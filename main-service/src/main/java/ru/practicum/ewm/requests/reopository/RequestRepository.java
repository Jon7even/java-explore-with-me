package ru.practicum.ewm.requests.reopository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.events.model.EventEntity;
import ru.practicum.ewm.requests.model.RequestEntity;
import ru.practicum.ewm.users.model.UserEntity;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
    List<RequestEntity> findAllByRequester(@Param("requester") UserEntity requester);

    boolean existsByRequesterAndEvent(@Param("requester") UserEntity requester, @Param("event") EventEntity event);

    @Query("SELECT rqs " +
            " FROM RequestEntity AS rqs " +
            " LEFT JOIN FETCH rqs.event " +
            " LEFT JOIN FETCH rqs.requester " +
            "WHERE rqs.event.initiator = :initiator " +
            "  AND rqs.event.id        = :eventId")
    List<RequestEntity> findAllByEventIdAndEventInitiator(@Param("eventId") Long eventId,
                                                          @Param("initiator") UserEntity initiator);

    List<RequestEntity> findAllByIdInAndEventInitiatorAndEvent(@Param("ids") List<Long> ids,
                                                               @Param("initiator") UserEntity initiator,
                                                               @Param("event") EventEntity event);
}
