package ru.practicum.ewm.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.stats.model.HitEntity;
import ru.practicum.ewm.stats.projections.HitTO;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<HitEntity, Long> {
    @Query("SELECT new ru.practicum.ewm.stats.projections.HitTO(ht.app, ht.uri, COUNT(ht.ip)) " +
            " FROM HitEntity AS ht " +
            "WHERE ht.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY ht.app, ht.uri " +
            "ORDER BY COUNT(DISTINCT ht.ip) " +
            " DESC")
    List<HitTO> findAllStatsByStartTimeAndEndTime(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.ewm.stats.projections.HitTO(ht.app, ht.uri, COUNT(ht.ip)) " +
            " FROM HitEntity AS ht " +
            "WHERE ht.timestamp BETWEEN ?1 AND ?2 " +
            "  AND ht.uri IN ?3 " +
            "GROUP BY ht.app, ht.uri " +
            "ORDER BY COUNT(DISTINCT ht.ip) " +
            " DESC")
    List<HitTO> findAllStatsByUriAndStartTimeAndEndTime(LocalDateTime start, LocalDateTime end, List<String> uri);

    @Query("SELECT new ru.practicum.ewm.stats.projections.HitTO(ht.app, ht.uri, COUNT(DISTINCT ht.ip)) " +
            " FROM HitEntity AS ht " +
            "WHERE ht.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY ht.app, ht.uri " +
            "ORDER BY COUNT(DISTINCT ht.ip) " +
            " DESC")
    List<HitTO> findAllUniqueStatsByStartTimeAndEndTime(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.ewm.stats.projections.HitTO(ht.app, ht.uri, COUNT(DISTINCT ht.ip)) " +
            " FROM HitEntity AS ht " +
            "WHERE ht.timestamp BETWEEN ?1 AND ?2 " +
            "  AND ht.uri IN ?3 " +
            "GROUP BY ht.app, ht.uri " +
            "ORDER BY COUNT(DISTINCT ht.ip) " +
            " DESC")
    List<HitTO> findAllUniqueStatsByUriAndStartTimeAndEndTime(LocalDateTime start, LocalDateTime end, List<String> uri);
}
