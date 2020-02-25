package com.example.integrationtestspringkafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@EnableKafka
public class ConsumerOfExampleDTOConfig {
	  @Value("${kafka.bootstrap-servers}")
	  private String bootstrapServers;

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, ExampleDTO> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ExampleDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, ExampleDTO> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer(ExampleDTO.class));
    }

}
