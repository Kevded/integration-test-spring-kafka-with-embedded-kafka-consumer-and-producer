package com.example.integrationtestspringkafka;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EmbeddedKafka(topics={"TOPIC_EXAMPLE"})
public class ProducerIntegrationTest {


	@Autowired
	private EmbeddedKafkaBroker embeddedKafkaBroker;
	
	@Autowired
	private Producer producer;

	
	@Test
    public void itShouldProduceCorrectExampleDTO(){
        // GIVEN
		ExampleDTO exampleDTO = new ExampleDTO();
		exampleDTO.setDescription("Une description");
		exampleDTO.setName("un nom");
		// simule an consumer
		Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("TOPIC_EXAMPLE", "false", embeddedKafkaBroker);
		consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		org.apache.kafka.clients.consumer.Consumer<String, ExampleDTO> consumer = new KafkaConsumer<>(consumerProps);
		embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "CONSUMER_TOPIC");
		
        // WHEN
		producer.send(exampleDTO);
        // THEN
		ConsumerRecord<String, ExampleDTO> consumerRecordOfExampleDTO = KafkaTestUtils.getSingleRecord(consumer, "TOPIC_EXAMPLE");
		ExampleDTO valueReceived = consumerRecordOfExampleDTO.value();
		
		// THEN
		assertEquals("Une description", valueReceived.getDescription());
		assertEquals("Un nom", valueReceived.getName());
		
    }
}
