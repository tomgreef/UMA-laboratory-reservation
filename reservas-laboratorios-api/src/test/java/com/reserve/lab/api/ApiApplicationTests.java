package com.reserve.lab.api;

import com.reserve.lab.api.config.EnableTestContainer;
import com.reserve.lab.api.config.TestContainerInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
@EnableTestContainer
@ContextConfiguration(initializers = {TestContainerInitializer.class})
class ApiApplicationTests {

    @Test
    void contextLoads() {
        assertTrue(true);
    }

}
