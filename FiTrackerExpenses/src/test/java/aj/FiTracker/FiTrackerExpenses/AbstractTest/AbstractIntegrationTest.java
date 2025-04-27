package aj.FiTracker.FiTrackerExpenses.AbstractTest;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;
import java.util.Map;

public abstract class AbstractIntegrationTest {
    private static final PostgreSQLTestContainer postgreSQLTestContainer;

    private static final KafkaTestContainer kafkaTestContainer;

    static {
        postgreSQLTestContainer = PostgreSQLTestContainer.getInstance();
        postgreSQLTestContainer.start();
        kafkaTestContainer = KafkaTestContainer.getInstance();

    }


    @DynamicPropertySource
    public static void registerProperties(DynamicPropertyRegistry registry) {

        PostgreSQLTestContainer.registerProperties(registry);
        KafkaTestContainer.setDynamicProperties(registry);
    }

    protected void truncateTable(String tableName) {
        AbstractIntegrationTest.postgreSQLTestContainer.truncateTable(tableName);
    }

    protected void insertTestData(String sql) {
        AbstractIntegrationTest.postgreSQLTestContainer.insertTestData(sql);
    }

    protected List<Map<String, Object>> getTestData(String sql) {
        return this.getTestData(sql, new Object[]{});
    }

    protected List<Map<String, Object>> getTestData(String sql, Object[] params) {
        return AbstractIntegrationTest.postgreSQLTestContainer.getTestData(sql, params);
    }
}
