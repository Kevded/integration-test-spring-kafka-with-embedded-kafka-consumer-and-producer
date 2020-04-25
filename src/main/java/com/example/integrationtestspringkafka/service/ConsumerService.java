package com.example.integrationtestspringkafka.service;

import com.example.integrationtestspringkafka.dto.ExampleDTO;
import com.example.integrationtestspringkafka.entity.ExampleEntity;
import com.example.integrationtestspringkafka.repository.ExampleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {
    Logger log = LoggerFactory.getLogger(ConsumerService.class);
    private ExampleRepository exampleRepository;

    ConsumerService(ExampleRepository exampleRepository) {
        this.exampleRepository = exampleRepository;
    }

    /**
     * Consume ExampleDTO on topic : TOPIC_EXAMPLE
     * Then save it in database.
     *
     * @param exampleDTO
     */
    @KafkaListener(topics = "TOPIC_EXAMPLE")
    public void consumeExampleDTO(ExampleDTO exampleDTO) {
        log.info("Received from topic=TOPIC_EXAMPLE ExampleDTO={}", exampleDTO);

        exampleRepository.save(convertToExampleEntity(exampleDTO));
        log.info("saved in database {}", exampleDTO);
    }

    /**
     * In Java world you should use an Mapper, or an dedicated service to do this.
     */
    public ExampleEntity convertToExampleEntity(ExampleDTO exampleDTO) {
        ExampleEntity exampleEntity = new ExampleEntity();
        exampleEntity.setDescription(exampleDTO.getDescription());
        exampleEntity.setName(exampleDTO.getName());
        return exampleEntity;
    }
}
