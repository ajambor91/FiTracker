package aj.FiTracker.FiTrackerExpenses;

import aj.FiTracker.FiTrackerExpenses.AbstractTest.AbstractIntegrationTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
@Tag("integration")
@SpringBootTest
class FiTrackerExpensesApplicationTests extends AbstractIntegrationTest {

    @Test
    void contextLoads() {
    }

}
