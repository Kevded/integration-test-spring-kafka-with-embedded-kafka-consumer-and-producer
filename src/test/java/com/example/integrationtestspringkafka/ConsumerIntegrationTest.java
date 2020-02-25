package com.example.integrationtestspringkafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EmbeddedKafka(topics = { "TOPIC_EXAMPLE" })
public class ConsumerIntegrationTest {

    @Test
    public void itShouldConsumeExampleDTO() throws Exception {
        // GIVEN
        // WHEN
        // THEN
        throw new Exception("not tested");
    }
}
