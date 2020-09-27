![Java CI](https://github.com/Kevded/integration-test-spring-kafka-with-embedded-kafka-consumer-and-producer/workflows/Java%20CI/badge.svg)

# Integration Test Spring Kafka with Embedded Kafka Consumer and Producer

Sample project to show how to implement Integration Test in Spring Boot. With Spring Kafka and EmbeddedKafka.

Example with Spring Boot 2.2.6 (Spring Kafka 2.4.5)

## Service
- [ConsumerService](src/main/java/com/example/integrationtestspringkafka/service/ConsumerService.java)
- [ProducerService](src/main/java/com/example/integrationtestspringkafka/service/ProducerService.java)

```java
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
     * @param exampleDTO {@link ExampleDTO}
     */
    @KafkaListener(topics = "TOPIC_EXAMPLE", groupId = "consumer_example_dto")
    public void consumeExampleDTO(ExampleDTO exampleDTO)  {
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
```

```java
@Service
public class ProducerService {
    Logger log = LoggerFactory.getLogger(ProducerService.class);

    private String topic = "TOPIC_EXAMPLE_EXTERNE";

    private KafkaTemplate<String, ExampleDTO> kafkaTemplate;

    ProducerService(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Send ExampleDTO to an external topic : TOPIC_EXAMPLE_EXTERNE.
     *
     * @param exampleDTO
     */
    public void send(ExampleDTO exampleDTO) {
        log.info("send to topic={} ExampleDTO={}", topic, exampleDTO);
        kafkaTemplate.send(topic, exampleDTO);
    }
}
```

## Integration test
- [ConsumerServiceIntegrationTest](src/test/java/com/example/integrationtestspringkafka/service/ConsumerServiceIntegrationTest.java)
- [ProducerServiceIntegrationTest](src/test/java/com/example/integrationtestspringkafka/service/ProducerServiceIntegrationTest.java)


```java
@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(topics = {"TOPIC_EXAMPLE", "TOPIC_EXAMPLE_EXTERNE"})
public class ConsumerServiceIntegrationTest {
    Logger log = LoggerFactory.getLogger(ConsumerServiceIntegrationTest.class);

    private static final String TOPIC_EXAMPLE = "TOPIC_EXAMPLE";

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private ExampleRepository exampleRepository;

    public ExampleDTO mockExampleDTO(String name, String description) {
        ExampleDTO exampleDTO = new ExampleDTO();
        exampleDTO.setDescription(description);
        exampleDTO.setName(name);
        return exampleDTO;
    }

    /**
     * We verify the output in the topic. But aslo in the database.
     */
    @Test
    public void itShould_ConsumeCorrectExampleDTO_from_TOPIC_EXAMPLE_and_should_saveCorrectExampleEntity() throws ExecutionException, InterruptedException {
        // GIVEN
        ExampleDTO exampleDTO = mockExampleDTO("Un nom 2", "Une description 2");
        // simulation consumer
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker.getBrokersAsString());
        log.info("props {}", producerProps);
        Producer<String, ExampleDTO> producerTest = new KafkaProducer(producerProps, new StringSerializer(), new JsonSerializer<ExampleDTO>());
        // Or
        // ProducerFactory producerFactory = new DefaultKafkaProducerFactory<String, ExampleDTO>(producerProps, new StringSerializer(), new JsonSerializer<ExampleDTO>());
        // Producer<String, ExampleDTO> producerTest = producerFactory.createProducer();
        // Or
        // ProducerRecord<String, ExampleDTO> producerRecord = new ProducerRecord<String, ExampleDTO>(TOPIC_EXAMPLE, "key", exampleDTO);
        // KafkaTemplate<String, ExampleDTO> template = new KafkaTemplate<>(producerFactory);
        // template.setDefaultTopic(TOPIC_EXAMPLE);
        // template.send(producerRecord);
        // WHEN
        producerTest.send(new ProducerRecord(TOPIC_EXAMPLE, "", exampleDTO));
        // THEN
        // we must have 1 entity inserted
        // We cannot predict when the insertion into the database will occur. So we wait until the value is present. Thank to Awaitility.
        await().atMost(Durations.TEN_SECONDS).untilAsserted(() -> {
            var exampleEntityList = exampleRepository.findAll();
            assertEquals(1, exampleEntityList.size());
            ExampleEntity firstEntity = exampleEntityList.get(0);
            assertEquals(exampleDTO.getDescription(), firstEntity.getDescription());
            assertEquals(exampleDTO.getName(), firstEntity.getName());
        });
        producerTest.close();
    }
}
```

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(topics = {"TOPIC_EXAMPLE", "TOPIC_EXAMPLE_EXTERNE"})
public class ProducerServiceIntegrationTest {
    private static final String TOPIC_EXAMPLE_EXTERNE = "TOPIC_EXAMPLE_EXTERNE";

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private ProducerService producerService;

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
    public void itShould_ProduceCorrectExampleDTO_to_TOPIC_EXAMPLE_EXTERNE() {
        // GIVEN
        ExampleDTO exampleDTO = mockExampleDTO("Un nom", "Une description");
        // simulation consumer
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("group_consumer_test", "false", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        ConsumerFactory cf = new DefaultKafkaConsumerFactory<String, ExampleDTO>(consumerProps, new StringDeserializer(), new JsonDeserializer<>(ExampleDTO.class, false));
        Consumer<String, ExampleDTO> consumerServiceTest = cf.createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumerServiceTest, TOPIC_EXAMPLE_EXTERNE);
        // WHEN
        producerService.send(exampleDTO);
        // THEN
        ConsumerRecord<String, ExampleDTO> consumerRecordOfExampleDTO = KafkaTestUtils.getSingleRecord(consumerServiceTest, TOPIC_EXAMPLE_EXTERNE);
        ExampleDTO valueReceived = consumerRecordOfExampleDTO.value();

        assertEquals("Une description", valueReceived.getDescription());
        assertEquals("Un nom", valueReceived.getName());

        consumerServiceTest.close();
    }
}
```
---

Official Example : [github.com/spring-projects/spring-kafka](https://github.com/spring-projects/spring-kafka)

To write integration tests you can also have a look at :

- [github.com/authorjapps/zerocode](https://github.com/authorjapps/zerocode)
- [github.com/authorjapps/zerocode/tree/master/kafka-testing](https://github.com/authorjapps/zerocode/tree/master/kafka-testing)

# Articles

- [gitbook.deddy.me/test-dintegration-avec-spring-kafka](https://gitbook.deddy.me/test-dintegration-avec-spring-kafka)