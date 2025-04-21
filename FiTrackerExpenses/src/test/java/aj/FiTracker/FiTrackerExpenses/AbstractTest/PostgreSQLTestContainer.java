package aj.FiTracker.FiTrackerExpenses.AbstractTest;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Testcontainers
public class PostgreSQLTestContainer extends PostgreSQLContainer<PostgreSQLTestContainer> {

    private static final String IMAGE_VERSION = "postgres:17.4-alpine3.21";
    @Container
    private static final PostgreSQLTestContainer container = new PostgreSQLTestContainer()
            .withUsername("testUser")
            .withPassword("testPassword")
            .withDatabaseName("fit");
    private Connection connection;

    private PostgreSQLTestContainer() {
        super(IMAGE_VERSION);
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

    public List<Map<String, Object>> getTestData(String sql, Object... params) {
        if (connection == null) {
            setConnection();
        }

        List<Map<String, Object>> results = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object columnValue = rs.getObject(i);
                        row.put(columnName, columnValue);
                    }
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing SQL query: " + sql, e);
        }

        return results;
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
