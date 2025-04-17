package aj.FiTracker.FiTrackerExpenses.AbstractTest;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Testcontainers
public class PostgreSQLTestContainer extends PostgreSQLContainer<PostgreSQLTestContainer> {

    private static final String IMAGE_VERSION = "postgres:17.4-alpine3.21";
    @Container
    private static final PostgreSQLTestContainer container = new PostgreSQLTestContainer()
            .withUsername("exampleUser")
            .withPassword("examplePassword")
            .withDatabaseName("fit");
    private Connection connection;

    private PostgreSQLTestContainer() {
        super(IMAGE_VERSION);
        this.start();
    }

    public static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    public static PostgreSQLTestContainer getInstance() {
        return PostgreSQLTestContainer.container;
    }

    public void truncateTable(String tableName) {
        if (connection == null) {
            setConnection();
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE " + tableName + " RESTART IDENTITY CASCADE;");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertTestData(String sql) {
        if (connection == null) {
            setConnection();
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setConnection() {
        try {
            connection = DriverManager.getConnection(
                    container.getJdbcUrl(),
                    container.getUsername(),
                    container.getPassword());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
