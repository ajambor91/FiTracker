package aj.FiTracker.FiTrackerExpenses.AbstractTest;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public abstract class AbstractIntegrationTest {
    private static final OAuthServerTestContainer oauServer;
    private static final KafkaTestContainer kafkaTestContainer;
    private static final PostgreSQLTestContainer postgreSQLTestContainer;

    static {
        oauServer = OAuthServerTestContainer.getInstance();
        kafkaTestContainer = KafkaTestContainer.getInstance();
        postgreSQLTestContainer = PostgreSQLTestContainer.getInstance();

    }


    @DynamicPropertySource
    public static void registerProps(DynamicPropertyRegistry props) {

        PostgreSQLTestContainer.registerProperties(props);
        OAuthServerTestContainer.registerProperties(props);
        KafkaTestContainer.setDynamicProperties(props);
    }

    protected void truncateTable(String tableName) {
        AbstractIntegrationTest.postgreSQLTestContainer.truncateTable(tableName);
    }

    protected void insertTestData(String sql) {
        AbstractIntegrationTest.postgreSQLTestContainer.insertTestData(sql);
    }
}
