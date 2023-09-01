package ru.practicum.ewm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EWMMainServiceTest {
    @Test
    void contextLoads() {
    }

    @Test
    void testMainService() {
        Assertions.assertDoesNotThrow(EWMMainServiceTest::new);
    }

}
