package com.example.integrationtestspringkafka.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EmbeddedKafka(topics = { "TOPIC_EXAMPLE" })
public class ConsumerServiceIntegrationTest {

    @Test
    public void itShouldConsumeExampleDTO()  {
        // TODO
        // GIVEN
        // WHEN
        // THEN
        assertFalse(false);
    }
}
