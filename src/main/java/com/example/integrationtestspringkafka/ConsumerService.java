package com.example.integrationtestspringkafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {
    Logger log = LoggerFactory.getLogger(ConsumerService.class);

    @KafkaListener(topics = "TOPIC_EXAMPLE")
    public void consumeExampleDTO(ExampleDTO exampleDTO) {
        log.info("Received from topic=TOPIC_EXAMPLE ExampleDTO={}" , exampleDTO);
    }
}
