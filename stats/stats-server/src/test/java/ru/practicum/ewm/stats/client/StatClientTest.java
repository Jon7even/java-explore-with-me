package ru.practicum.ewm.stats.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.ewm.stats.controller.StatController;
import ru.practicum.ewm.stats.dto.RequestStatListTO;
import ru.practicum.ewm.stats.setup.GenericTests;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StatClientTest extends GenericTests {
    @Autowired
    private StatController controller;

    @Autowired
    private StatClient statClient;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        initHitDTO();
        initVariables();
    }

    @Test
    @DisplayName("Сохранить посещение с помощью клиента")
    void shouldCreateHitByClient_thenStatus201() throws Exception {
        ResponseEntity<Object> result = statClient.createHit(hitOne);
        assertEquals(result.getStatusCodeValue(), 201);
    }

    @Test
    @DisplayName("Запросить статистику с помощью клиента Uri пустой")
    void shouldGetStatsByClientUriEmpty_thenStatus200() throws Exception {
        statClient.createHit(hitOne);
        String original = "[{\"app\":\"ewm-test-1\",\"uri\":\"test/test/test/1\",\"hits\":1}]";

        RequestStatListTO requestStatList = RequestStatListTO.builder()
                .start(start.minusHours(1))
                .end(end.plusHours(1))
                .uris(uris)
                .unique(true)
                .build();
        ResponseEntity<Object> result = statClient.getStats(requestStatList);

        assertEquals(result.getStatusCodeValue(), 200);
        assertEquals(objectMapper.writeValueAsString(result.getBody()), original);
    }

    @Test
    @DisplayName("Запросить статистику с помощью клиента Uri со значениями")
    void shouldGetStatsByClientUriNotEmpty_thenStatus200() throws Exception {
        statClient.createHit(hitSecond);
        String original = "[{\"app\":\"ewm-test-2\",\"uri\":\"test/test/test/2\",\"hits\":1}]";

        RequestStatListTO requestStatList = RequestStatListTO.builder()
                .start(start.minusYears(1))
                .end(end.plusYears(1))
                .uris(List.of(hitSecond.getUri()))
                .unique(false)
                .build();
        ResponseEntity<Object> result = statClient.getStats(requestStatList);

        assertEquals(result.getStatusCodeValue(), 200);
        assertEquals(objectMapper.writeValueAsString(result.getBody()), original);
    }

}
