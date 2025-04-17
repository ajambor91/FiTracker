package aj.FiTracker.FiTracker.AbstractTest;

import aj.FiTracker.FiTracker.Documents.Zone;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public abstract class AbstractIntegrationTest {
    private static final MongoDBTestContainer mongoTestContainer;
    private static final KafkaTestContainer kafkaTestContainer;

    static {
        mongoTestContainer = MongoDBTestContainer.getInstance();
        kafkaTestContainer = KafkaTestContainer.getInstance();
    }


    @DynamicPropertySource
    public static void registerProps(DynamicPropertyRegistry props) {

        MongoDBTestContainer.registerProperties(props);
        KafkaTestContainer.setDynamicProperties(props);
    }

    protected void insertTestDataIntoDB(Zone zone) {
        mongoTestContainer.addTestZoneIntoDB(zone);
    }

    protected void dropZones() {
        mongoTestContainer.truncateCollection();
    }
}
