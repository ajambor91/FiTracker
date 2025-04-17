package aj.FiTracker.FiTracker.Abstract;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public abstract class AbstractIntegrationTest {
    private static final PostgreSQLTestContainer postgreSQLTestContainer;
    private static final VaultTestContainer vaultContainer;

    static {
        vaultContainer = VaultTestContainer.getInstance();
        postgreSQLTestContainer = PostgreSQLTestContainer.getInstance();
        postgreSQLTestContainer.start();
        vaultContainer.start();

    }

    protected AbstractIntegrationTest() {
    }

    @DynamicPropertySource
    public static void registerProperties(DynamicPropertyRegistry registry) throws Exception {
        PostgreSQLTestContainer.registerProperties(registry);
        VaultTestContainer.vaultProperties(registry);
    }

    protected void truncateTable(String tableName) {
        AbstractIntegrationTest.postgreSQLTestContainer.truncateTable(tableName);
    }

    protected void insertTestData(String sql) {
        AbstractIntegrationTest.postgreSQLTestContainer.insertTestData(sql);
    }

}
