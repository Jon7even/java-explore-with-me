package ru.practicum.ewm.rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.events.model.EventEntity;
import ru.practicum.ewm.rating.model.RatingEntity;
import ru.practicum.ewm.rating.model.RatingId;
import ru.practicum.ewm.users.model.UserEntity;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<RatingEntity, RatingId> {
    void deleteByIdLikerAndIdEvent(@Param("liker") UserEntity liker, @Param("event") EventEntity event);

    boolean existsByIdLikerAndIdEvent(@Param("liker") UserEntity liker, @Param("event") EventEntity event);

    Optional<RatingEntity> findByIdLikerAndIdEvent(@Param("liker") UserEntity liker, @Param("event") EventEntity event);
}
