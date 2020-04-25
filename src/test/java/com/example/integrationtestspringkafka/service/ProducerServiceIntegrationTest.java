package com.example.integrationtestspringkafka.service;

import com.example.integrationtestspringkafka.dto.ExampleDTO;
import com.example.integrationtestspringkafka.entity.ExampleEntity;
import com.example.integrationtestspringkafka.repository.ExampleRepository;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EmbeddedKafka(topics = {"TOPIC_EXAMPLE"})
public class ProducerServiceIntegrationTest {
    private static final String TOPIC_EXAMPLE = "TOPIC_EXAMPLE";

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private ProducerService producerService;

    @Autowired
    private ExampleRepository exampleRepository;

    public ExampleDTO mockExampleDTO(String name, String description) {
        ExampleDTO exampleDTO = new ExampleDTO();
        exampleDTO.setDescription(description);
        exampleDTO.setName(name);
        return exampleDTO;
    }

    /**
     * We verify the output in the topic. With an simulated consumer.
     */
    @Test
    public void itShould_ProduceCorrectExampleDTO_to_TOPIC_EXAMPLE() {
        // GIVEN
        ExampleDTO exampleDTO = mockExampleDTO("Un nom", "Une description");
        // simulation consumer
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("group_consumer_test", "false", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        ConsumerFactory cf = new DefaultKafkaConsumerFactory<String, ExampleDTO>(consumerProps, new StringDeserializer(), new JsonDeserializer<>(ExampleDTO.class, false));
        Consumer<String, ExampleDTO> consumerServiceTest = cf.createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumerServiceTest, TOPIC_EXAMPLE);
        // WHEN
        producerService.send(exampleDTO);
        // THEN
        ConsumerRecord<String, ExampleDTO> consumerRecordOfExampleDTO = KafkaTestUtils.getSingleRecord(consumerServiceTest, TOPIC_EXAMPLE);
        ExampleDTO valueReceived = consumerRecordOfExampleDTO.value();

        assertEquals("Une description", valueReceived.getDescription());
        assertEquals("Un nom", valueReceived.getName());
    }

    /**
     * We verify the output in the topic. But aslo in the database.
     */
    @Test
    public void itShould_ProduceCorrectExampleDTO_to_TOPIC_EXAMPLE_and_should_saveCorrectExampleEntity() {
        // GIVEN
        ExampleDTO exampleDTO = mockExampleDTO("Un nom 2", "Une description 2");
        // simulation consumer
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("group_consumer_test_2", "false", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        ConsumerFactory cf = new DefaultKafkaConsumerFactory<String, ExampleDTO>(consumerProps, new StringDeserializer(), new JsonDeserializer<>(ExampleDTO.class, false));
        Consumer<String, ExampleDTO> consumerServiceTest = cf.createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumerServiceTest, TOPIC_EXAMPLE);
        // WHEN
        producerService.send(exampleDTO);
        // THEN verify output in topic
        ConsumerRecord<String, ExampleDTO> consumerRecordOfExampleDTO = KafkaTestUtils.getSingleRecord(consumerServiceTest, TOPIC_EXAMPLE);
        ExampleDTO valueReceived = consumerRecordOfExampleDTO.value();

        assertEquals("Une description 2", valueReceived.getDescription());
        assertEquals("Un nom 2", valueReceived.getName());

        // THEN verify output in databse
        List<ExampleEntity> exampleEntityList = exampleRepository.findAll();
        // we must have 1 entity inserted
        // We cannot predict when the insertion into the database will occur. So we wait until the value is present. Thank to Awaitility.
        await().untilAsserted(() -> {
            assertEquals(1, exampleEntityList.size());

            ExampleEntity firstEntity = exampleEntityList.get(0);

            assertEquals(exampleDTO.getDescription(), firstEntity.getDescription());
            assertEquals(exampleDTO.getName(), firstEntity.getName());
        });
    }
}
