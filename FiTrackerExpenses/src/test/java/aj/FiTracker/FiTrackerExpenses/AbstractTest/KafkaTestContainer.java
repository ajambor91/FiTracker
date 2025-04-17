package aj.FiTracker.FiTrackerExpenses.AbstractTest;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class KafkaTestContainer extends ConfluentKafkaContainer {
    private static final String IMAGE_NAME = "confluentinc/cp-kafka:7.4.0";
    private static KafkaTestContainer instance;

    private KafkaTestContainer() {
        super(DockerImageName.parse(IMAGE_NAME));
        this.start();
    }

    public static KafkaTestContainer getInstance() {
        if (instance == null) {
            instance = new KafkaTestContainer();
        }
        return instance;
    }

    public static void setDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", instance::getBootstrapServers);
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
        registry.add("spring.kafka.producer.key-serializer", () -> "org.apache.kafka.common.serialization.StringSerializer");
        registry.add("spring.kafka.producer.value-serializer", () -> "org.apache.kafka.common.serialization.StringSerializer");
        registry.add("spring.kafka.consumer.key-deserializer", () -> "org.apache.kafka.common.serialization.StringDeserializer");
        registry.add("spring.kafka.consumer.value-deserializer", () -> "org.apache.kafka.common.serialization.StringDeserializer");
        registry.add("spring.kafka.consumer.properties.group.instance.id", () -> "consumer-zone");
        registry.add("spring.kafka.consumer.session.timeout.ms", () -> "60000");
        registry.add("spring.kafka.consumer.heartbeat.interval.ms", () -> "20000");
    }

}
