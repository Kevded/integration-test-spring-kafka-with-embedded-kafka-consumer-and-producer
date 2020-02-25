package com.example.integrationtestspringkafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class Consumer {

	@KafkaListener(topics = "TOPIC_EXAMPLE")
	public void consumeExampleDTO(ExampleDTO exampleDTO) {
		System.out.println("Received from topic=TOPIC_EXAMPLE ExampleDTO="+exampleDTO);
	}
}
