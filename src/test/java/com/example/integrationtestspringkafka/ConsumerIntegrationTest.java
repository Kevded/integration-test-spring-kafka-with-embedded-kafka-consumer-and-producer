package com.example.integrationtestspringkafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@EmbeddedKafka(topics={"TOPIC_EXAMPLE"})
public class ConsumerIntegrationTest {


    @Test
    public void itShouldConsumeExampleDTO(){
        // GIVEN
        // WHEN
        // THEN
    }
}
