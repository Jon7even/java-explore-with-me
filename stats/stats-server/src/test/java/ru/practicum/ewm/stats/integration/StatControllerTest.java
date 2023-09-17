package ru.practicum.ewm.stats.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.stats.setup.GenericTests;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.stats.dto.constants.Constants.DEFAULT_TIME_FORMAT;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StatControllerTest extends GenericTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        initHitDTO();
        initVariables();
    }

    @Test
    @DisplayName("Посещение должно сохраниться")
    void shouldCreateHit_thenStatus201() throws Exception {
        mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(hitOne))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Запросить статистику и получить пустой список")
    void shouldGetStatsReturnEmptyList_thenStatus200() throws Exception {
        mockMvc.perform(get("/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                        start.format(DEFAULT_TIME_FORMAT), end.format(DEFAULT_TIME_FORMAT), uris, true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Запросить статистику и перепутать старт с окончанием")
    void shouldGetStatsByIncorrectDataTimeFormat_thenStatus400() throws Exception {
        mockMvc.perform(get("/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                        end.format(DEFAULT_TIME_FORMAT), start.format(DEFAULT_TIME_FORMAT), uris, true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Запросить статистику и корректно получить ее")
    void shouldGetStatsValidList_thenStatus200() throws Exception {
        mockMvc.perform(post("/hit")
                        .content(objectMapper.writeValueAsString(hitOne))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/stats?start={start}&end={end}&unique={unique}",
                        start.minusHours(1).format(DEFAULT_TIME_FORMAT),
                        end.format(DEFAULT_TIME_FORMAT), true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].app").value(hitOne.getApp()))
                .andExpect(jsonPath("$[0].uri").value(hitOne.getUri()))
                .andExpect(jsonPath("$[0].hits").value(1));
    }

}