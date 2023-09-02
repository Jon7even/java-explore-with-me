package ru.practicum.ewm.stats.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.ewm.stats.mapper.StatMapper;
import ru.practicum.ewm.stats.model.HitEntity;
import ru.practicum.ewm.stats.projections.HitTO;
import ru.practicum.ewm.stats.setup.GenericTests;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StatRepositoryTest extends GenericTests {
    @Autowired
    private StatRepository repository;

    private HitEntity hitInDbOne;

    private HitEntity hitInDbSecond;

    @BeforeEach
    void setUp() {
        initHitDTO();
        hitInDbOne = repository.save(StatMapper.toHitEntity(hitOne));
        hitInDbSecond = repository.save(StatMapper.toHitEntity(hitSecond));

        repository.save(StatMapper.toHitEntity(hitOne));
        repository.save(StatMapper.toHitEntity(hitSecond));
    }

    @AfterEach
    void clearRepository() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Корректное сохранение сущности")
    void saveHit() {
        assertNotNull(hitInDbOne);
        assertEquals(1, hitInDbOne.getId());
        assertEquals(hitOne.getApp(), hitInDbOne.getApp());
        assertEquals(hitOne.getUri(), hitInDbOne.getUri());
        assertEquals(hitOne.getIp(), hitInDbOne.getIp());
        assertEquals(hitOne.getTimestamp(), hitInDbOne.getTimestamp());

        assertNotNull(hitInDbSecond);
        assertEquals(2, hitInDbSecond.getId());
        assertEquals(hitSecond.getApp(), hitInDbSecond.getApp());
        assertEquals(hitSecond.getUri(), hitInDbSecond.getUri());
        assertEquals(hitSecond.getIp(), hitInDbSecond.getIp());
        assertEquals(hitSecond.getTimestamp(), hitInDbSecond.getTimestamp());

        assertEquals(repository.findAll().size(), 4);
    }

    @Test
    @DisplayName("Найти всю статистику без уникальности и без Uri")
    void findAllStatsByStartTimeAndEndTime() {
        List<HitTO> resultSizeOne = repository.findAllStatsByStartTimeAndEndTime(
                hitOne.getTimestamp().minusHours(1), hitOne.getTimestamp().plusMinutes(30)
        );

        assertEquals(resultSizeOne.size(), 1);
        assertEquals(resultSizeOne.get(0).getApp(), hitOne.getApp());
        assertEquals(resultSizeOne.get(0).getHits(), 2);

        List<HitTO> resultSizeSecond = repository.findAllStatsByStartTimeAndEndTime(
                hitOne.getTimestamp().minusHours(1), hitOne.getTimestamp().plusHours(2)
        );

        assertEquals(resultSizeSecond.size(), 2);
        assertEquals(resultSizeSecond.get(0).getApp(), hitOne.getApp());
        assertEquals(resultSizeSecond.get(0).getHits(), 2);
        assertEquals(resultSizeSecond.get(1).getApp(), hitSecond.getApp());
        assertEquals(resultSizeSecond.get(1).getHits(), 2);
    }

    @Test
    @DisplayName("Найти всю статистику без уникальности и с Uri")
    void findAllStatsByUriAndStartTimeAndEndTime() {
        List<HitTO> resultSizeOne = repository.findAllStatsByUriAndStartTimeAndEndTime(
                hitOne.getTimestamp().minusHours(1), hitOne.getTimestamp().plusMinutes(30), List.of(hitOne.getUri())
        );

        assertEquals(resultSizeOne.size(), 1);
        assertEquals(resultSizeOne.get(0).getApp(), hitOne.getApp());
        assertEquals(resultSizeOne.get(0).getHits(), 2);

        List<HitTO> resultSizeSecond = repository.findAllStatsByUriAndStartTimeAndEndTime(
                hitOne.getTimestamp().minusHours(1), hitOne.getTimestamp().plusHours(2),
                List.of(hitOne.getUri(), hitSecond.getUri())
        );

        assertEquals(resultSizeSecond.size(), 2);
        assertEquals(resultSizeSecond.get(0).getApp(), hitOne.getApp());
        assertEquals(resultSizeSecond.get(0).getHits(), 2);
        assertEquals(resultSizeSecond.get(1).getApp(), hitSecond.getApp());
        assertEquals(resultSizeSecond.get(1).getHits(), 2);
    }

    @Test
    @DisplayName("Найти всю статистику c уникальностью и без Uri")
    void findAllUniqueStatsByStartTimeAndEndTime() {
        List<HitTO> resultSizeOne = repository.findAllUniqueStatsByStartTimeAndEndTime(
                hitOne.getTimestamp().minusHours(1), hitOne.getTimestamp().plusMinutes(30)
        );

        assertEquals(resultSizeOne.size(), 1);
        assertEquals(resultSizeOne.get(0).getApp(), hitOne.getApp());
        assertEquals(resultSizeOne.get(0).getHits(), 1);

        List<HitTO> resultSizeSecond = repository.findAllUniqueStatsByStartTimeAndEndTime(
                hitOne.getTimestamp().minusHours(1), hitOne.getTimestamp().plusHours(2)
        );

        assertEquals(resultSizeSecond.size(), 2);
        assertEquals(resultSizeSecond.get(0).getApp(), hitOne.getApp());
        assertEquals(resultSizeSecond.get(0).getHits(), 1);
        assertEquals(resultSizeSecond.get(1).getApp(), hitSecond.getApp());
        assertEquals(resultSizeSecond.get(1).getHits(), 1);
    }

    @Test
    @DisplayName("Найти всю статистику c уникальностью и с Uri")
    void findAllUniqueStatsByUriAndStartTimeAndEndTime() {
        List<HitTO> resultSizeOne = repository.findAllUniqueStatsByUriAndStartTimeAndEndTime(
                hitOne.getTimestamp().minusHours(1), hitOne.getTimestamp().plusMinutes(30), List.of(hitOne.getUri())
        );

        assertEquals(resultSizeOne.size(), 1);
        assertEquals(resultSizeOne.get(0).getApp(), hitOne.getApp());
        assertEquals(resultSizeOne.get(0).getHits(), 1);

        List<HitTO> resultSizeSecond = repository.findAllUniqueStatsByUriAndStartTimeAndEndTime(
                hitOne.getTimestamp().minusHours(1), hitOne.getTimestamp().plusHours(2),
                List.of(hitOne.getUri(), hitSecond.getUri())
        );

        assertEquals(resultSizeSecond.size(), 2);
        assertEquals(resultSizeSecond.get(0).getApp(), hitOne.getApp());
        assertEquals(resultSizeSecond.get(0).getHits(), 1);
        assertEquals(resultSizeSecond.get(1).getApp(), hitSecond.getApp());
        assertEquals(resultSizeSecond.get(1).getHits(), 1);
    }

}
