package com.example.integrationtestspringkafka.repository;

import com.example.integrationtestspringkafka.entity.ExampleEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExampleRepository extends CrudRepository<ExampleEntity, Long> {
    List<ExampleEntity> findAll();
}
