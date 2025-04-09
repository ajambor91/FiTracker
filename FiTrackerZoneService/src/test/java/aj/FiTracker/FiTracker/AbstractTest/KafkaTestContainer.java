package aj.FiTracker.FiTracker.AbstractTest;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class KafkaTestContainer extends ConfluentKafkaContainer {
    private static KafkaTestContainer instance;
    private static final String IMAGE_NAME = "confluentinc/cp-kafka:7.4.0";
    private KafkaTestContainer() {
        super(DockerImageName.parse(IMAGE_NAME));
//        this.withEnv("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "BROKER:PLAINTEXT,PLAINTEXT:PLAINTEXT,CONTROLLER:PLAINTEXT");
//        this.withEnv("KAFKA_LISTENERS", "PLAINTEXT://0.0.0.0:9092,BROKER://0.0.0.0:9093,CONTROLLER://0.0.0.0:9094");
//        this.withEnv("KAFKA_ADVERTISED_LISTENERS",
//                "PLAINTEXT://host.testcontainers.internal:9092," +
//                        "BROKER://host.testcontainers.internal:9093," +
//                        "CONTROLLER://host.testcontainers.internal:9094");
//        this.withEnv("KAFKA_CONTROLLER_LISTENER_NAMES", "CONTROLLER");
//        this.withEnv("KAFKA_PROCESS_ROLES", "broker,controller");
//        this.withEnv("KAFKA_NODE_ID", "1");
//        this.withEnv("KAFKA_CLUSTER_ID", "4L6g3nShT-eMCtK--X86sw");
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
