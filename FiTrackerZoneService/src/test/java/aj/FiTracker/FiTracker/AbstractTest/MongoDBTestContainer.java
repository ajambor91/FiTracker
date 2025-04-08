package aj.FiTracker.FiTracker.AbstractTest;

import aj.FiTracker.FiTracker.Documents.Zone;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


@Testcontainers
public class MongoDBTestContainer extends MongoDBContainer {

    private static final String IMAGE_VERSION = "mongo:8.0.6";
    private static final String DATABASE_NAME = "testdb";
    private static MongoDBTestContainer instance;
    private final MongoTemplate mongoTemplate;

    private MongoDBTestContainer() {
        super(DockerImageName.parse(IMAGE_VERSION));
        this.start();
        MongoClient mongoClient = MongoClients.create(this.getConnectionString());
        mongoTemplate = new MongoTemplate(mongoClient, DATABASE_NAME);
    }

    public static MongoDBTestContainer getInstance() {
        if (instance == null) {
            instance = new MongoDBTestContainer();
        }
        return instance;
    }

    public static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", instance::getConnectionString);
        registry.add("spring.data.mongodb.database", () -> DATABASE_NAME);

    }

    public void addTestZoneIntoDB(Zone zone) {
        if (mongoTemplate != null) {
            mongoTemplate.insert(zone, "zone");
        } else {
            throw new IllegalStateException("MongoTemplate was not initialized.");
        }
    }

    public void truncateCollection() {
        if (mongoTemplate != null) {
            mongoTemplate.dropCollection("zone");
        } else {
            throw new IllegalStateException("MongoTemplate was not initialized.");
        }
    }
}
