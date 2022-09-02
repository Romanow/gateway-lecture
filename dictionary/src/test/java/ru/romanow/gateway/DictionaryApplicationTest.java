package ru.romanow.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.romanow.gateway.config.DatabaseTestConfiguration;

@ActiveProfiles("test")
@SpringBootTest
@Import(DatabaseTestConfiguration.class)
class DictionaryApplicationTest {

    @Test
    void test() {
    }

}