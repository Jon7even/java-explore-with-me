package ru.practicum.ewm.stats.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.practicum.ewm.stats.dto.RequestStatListTO;
import ru.practicum.ewm.stats.model.HitRequest;
import ru.practicum.ewm.stats.setup.GenericTests;
import ru.practicum.ewm.stats.utils.ConverterRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConverterRequestTest extends GenericTests {
    @BeforeEach
    void setUp() {
        initVariables();
    }

    @Test
    @DisplayName("Ожидается ENUM без учета уникальности и без Uri")
    void shouldReturnValidRequestAllStatsWithoutUri() {
        HitRequest expected = HitRequest.ALL_STATS;

        RequestStatListTO original = RequestStatListTO.builder()
                .start(start)
                .end(end)
                .uris(uris)
                .unique(false)
                .build();

        HitRequest result = ConverterRequest.getHitRequest(original);

        assertNotNull(result);
        assertEquals(expected, result);

        RequestStatListTO originalUrisIsNull = RequestStatListTO.builder()
                .start(start)
                .end(end)
                .unique(false)
                .build();

        HitRequest resultUrisIsNull = ConverterRequest.getHitRequest(originalUrisIsNull);

        assertNotNull(resultUrisIsNull);
        assertEquals(expected, resultUrisIsNull);
    }

    @Test
    @DisplayName("Ожидается ENUM с учетом уникальности и без Uri")
    void shouldReturnValidRequestAllUniqueStatsWithoutUri() {
        HitRequest expected = HitRequest.ALL_UNIQUE_STATS;

        RequestStatListTO original = RequestStatListTO.builder()
                .start(start)
                .end(end)
                .uris(uris)
                .unique(true)
                .build();

        HitRequest result = ConverterRequest.getHitRequest(original);

        assertNotNull(result);
        assertEquals(expected, result);

        RequestStatListTO originalUrisIsNull = RequestStatListTO.builder()
                .start(start)
                .end(end)
                .unique(true)
                .build();

        HitRequest resultUrisIsNull = ConverterRequest.getHitRequest(originalUrisIsNull);

        assertNotNull(resultUrisIsNull);
        assertEquals(expected, resultUrisIsNull);
    }

    @Test
    @DisplayName("Ожидается ENUM без учета уникальности и с Uri")
    void shouldReturnValidRequestAllStatsWithUri() {
        HitRequest expected = HitRequest.URI_STATS;

        RequestStatListTO original = RequestStatListTO.builder()
                .start(start)
                .end(end)
                .uris(List.of("test/test/test"))
                .unique(false)
                .build();

        HitRequest result = ConverterRequest.getHitRequest(original);

        assertNotNull(result);
        assertEquals(expected, result);

        RequestStatListTO originalUrisSizeTwo = RequestStatListTO.builder()
                .start(start)
                .end(end)
                .uris(List.of("test/test/test", "test/test"))
                .unique(false)
                .build();

        assertEquals(originalUrisSizeTwo.getUris().size(), 2);

        HitRequest resultUrisIsNull = ConverterRequest.getHitRequest(originalUrisSizeTwo);

        assertNotNull(resultUrisIsNull);
        assertEquals(expected, resultUrisIsNull);
    }

    @Test
    @DisplayName("Ожидается ENUM с учетом уникальности и c Uri")
    void shouldReturnValidRequestAllUniqueStatsWithUri() {
        HitRequest expected = HitRequest.URI_UNIQUE_STATS;

        RequestStatListTO original = RequestStatListTO.builder()
                .start(start)
                .end(end)
                .uris(List.of("test/test/test"))
                .unique(true)
                .build();

        HitRequest result = ConverterRequest.getHitRequest(original);

        assertNotNull(result);
        assertEquals(expected, result);

        RequestStatListTO originalUrisSizeTwo = RequestStatListTO.builder()
                .start(start)
                .end(end)
                .uris(List.of("test/test/test", "test/test"))
                .unique(true)
                .build();

        assertEquals(originalUrisSizeTwo.getUris().size(), 2);

        HitRequest resultUrisIsNull = ConverterRequest.getHitRequest(originalUrisSizeTwo);

        assertNotNull(resultUrisIsNull);
        assertEquals(expected, resultUrisIsNull);
    }

}
