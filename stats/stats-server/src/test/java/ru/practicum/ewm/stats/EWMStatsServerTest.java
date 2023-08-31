package ru.practicum.ewm.stats;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EWMStatsServerTest {

    @Test
    void testMain() {
        Assertions.assertDoesNotThrow(EWMStatsServerApp::new);
    }

}
