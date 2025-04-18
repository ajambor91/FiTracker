package aj.FiTracker.FiTrackerExpenses.AbstractTest;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public abstract class AbstractIntegrationTest {
    private static final KafkaTestContainer kafkaTestContainer;
    private static final PostgreSQLTestContainer postgreSQLTestContainer;

    static {
        kafkaTestContainer = KafkaTestContainer.getInstance();
        postgreSQLTestContainer = PostgreSQLTestContainer.getInstance();
        postgreSQLTestContainer.start();

    }


    @DynamicPropertySource
    public static void registerProps(DynamicPropertyRegistry props) {

        PostgreSQLTestContainer.registerProperties(props);
        KafkaTestContainer.setDynamicProperties(props);
    }

    protected void truncateTable(String tableName) {
        AbstractIntegrationTest.postgreSQLTestContainer.truncateTable(tableName);
    }

    protected void insertTestData(String sql) {
        AbstractIntegrationTest.postgreSQLTestContainer.insertTestData(sql);
    }
}
