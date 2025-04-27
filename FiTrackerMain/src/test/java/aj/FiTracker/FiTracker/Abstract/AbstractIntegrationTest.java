package aj.FiTracker.FiTracker.Abstract;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public abstract class AbstractIntegrationTest {
    private static final PostgreSQLTestContainer postgreSQLTestContainer;
    private static final VaultTestContainer vaultContainer;
    private static final KafkaTestContainer kafkaContainer;

    static {
        vaultContainer = VaultTestContainer.getInstance();
        postgreSQLTestContainer = PostgreSQLTestContainer.getInstance();
        kafkaContainer = KafkaTestContainer.getInstance();

        postgreSQLTestContainer.start();
        vaultContainer.start();

    }

    protected AbstractIntegrationTest() {
    }

    @DynamicPropertySource
    public static void registerProperties(DynamicPropertyRegistry registry) throws Exception {
        PostgreSQLTestContainer.registerProperties(registry);
        VaultTestContainer.vaultProperties(registry);
        KafkaTestContainer.setDynamicProperties(registry);
    }

    protected void truncateTable(String tableName) {
        AbstractIntegrationTest.postgreSQLTestContainer.truncateTable(tableName);
    }

    private void insertTestData(String sql) {
        AbstractIntegrationTest.postgreSQLTestContainer.insertTestData(sql);
    }

    protected void insertTestUserIntoDataBase() {
        this.insertTestData(
                "INSERT INTO app_core.app_user (id, login, name, password,email, salt, unique_id, created_at, updated_at) " +
                        "VALUES (1,'testLogin', 'Test User', '$2a$10$JWWX4sPfFPl84AeiYeQm5eA.EEmNbALPjKYyGiP2qG/Q3t8.8fQ4a', 'test@mail.com', " +
                        "'xAcJlQ5mjvc6QsK0AF+hkA==', '194e36b4-2f25-4171-83e3-8543bfcd54f4', NOW(), NOW())"
        );
    }

    protected void insertTestFirstUserWithIncorrectPassword() {
        this.insertTestData(
                "INSERT INTO app_core.app_user (id, login, name, password,email, salt, unique_id, created_at, updated_at) " +
                        "VALUES (1,'testLogin', 'Test User', '$2a$10$JWWX4sPfFPl84AeiYeQm5eA.EEmNbALPjKYyGiP2qG/Q3t8.8fQ4a', 'test@mail.com', " +
                        "'xAcJlQ5mjvc6QsK0AF+hkA==', '194e36b4-2f25-4171-83e3-8543bfcd54f4', NOW(), NOW())"
        );
    }

    protected void insertTestUserWithIncorrectPassword() {
        this.insertTestData(
                "INSERT INTO app_core.app_user (id, login, name, password, email, salt, unique_id, created_at, updated_at) " +
                        "VALUES (2,'testSecondLogin', 'Test SecondUser', 'IncorrectPassword', " +
                        "'test2@mail.com','xAcJlQ5mjvc6QsK0AF+hkA==', 'e7058eb5-3b8e-41f7-a972-c039097d7529', NOW(), NOW())"
        );
    }

}
