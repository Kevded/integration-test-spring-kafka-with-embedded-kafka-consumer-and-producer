package com.example.integrationtestspringkafka.service;

import com.example.integrationtestspringkafka.dto.ExampleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {
    Logger log = LoggerFactory.getLogger(ProducerService.class);

    private String topic = "TOPIC_EXAMPLE";

    private KafkaTemplate<String, ExampleDTO> kafkaTemplate;

    ProducerService(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(ExampleDTO exampleDTO) {
        log.info("send to topic={} ExampleDTO={}", topic, exampleDTO);
        kafkaTemplate.send(topic, exampleDTO);
    }
}
