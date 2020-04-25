package com.example.integrationtestspringkafka.repository;

import com.example.integrationtestspringkafka.entity.ExampleEntity;
import org.springframework.data.repository.CrudRepository;

public interface ExampleRepository extends CrudRepository<ExampleEntity, Long> {

}
