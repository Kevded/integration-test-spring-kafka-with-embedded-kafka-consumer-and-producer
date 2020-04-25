package com.example.integrationtestspringkafka.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.example.integrationtestspringkafka.repository")
public class DatabaseConfig {
}
