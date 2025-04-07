package aj.FiTracker.FiTracker.AbstractTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


@Testcontainers
public class MongoDBTestContainer extends MongoDBContainer {

    private static final String IMAGE_VERSION = "mongo:8.0.6";
    private static MongoDBTestContainer instance;


    private MongoDBTestContainer() {
        super(DockerImageName.parse(IMAGE_VERSION));
        this.start();
    }

    public static MongoDBTestContainer getInstance() {
        if (instance == null) {
            instance = new MongoDBTestContainer();
        }
        return instance;
    }

    public static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", instance::getConnectionString);
    }
}
