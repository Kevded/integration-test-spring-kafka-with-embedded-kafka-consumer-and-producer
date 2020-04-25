package com.example.integrationtestspringkafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@EmbeddedKafka(topics = {"TOPIC_EXAMPLE"})
class IntegrationtestspringkafkaApplicationTests {

    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> IntegrationtestspringkafkaApplication.main(new String[]{}));
    }

}
