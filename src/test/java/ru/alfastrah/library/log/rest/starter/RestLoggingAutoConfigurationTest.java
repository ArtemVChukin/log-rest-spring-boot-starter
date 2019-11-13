package ru.alfastrah.library.log.rest.starter;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {RestLoggingAutoConfiguration.class})
class RestLoggingAutoConfigurationTest {

    @DisplayName("Тест загрузки конфигурации в контекст")
    @Test
    void contextLoads() {
        Assert.assertTrue(true);
    }
}
