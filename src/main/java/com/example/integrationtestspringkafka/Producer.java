package com.example.integrationtestspringkafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {
	private String topic = "TOPIC_EXAMPLE";
	
	private KafkaTemplate<String, ExampleDTO> kafkaTemplate;
	
	Producer(KafkaTemplate kafkaTemplate){
		this.kafkaTemplate = kafkaTemplate;	
	}
	
	public void send(ExampleDTO exampleDTO) {
		System.out.println("send to topic=TOPIC_EXAMPLE ExampleDTO=" + exampleDTO);
		kafkaTemplate.send(topic, exampleDTO);
	}
}