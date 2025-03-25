package aj.FiTracker.FiTracker;

import aj.FiTracker.FiTracker.Abstract.AbstractIntegrationTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
@Tag("integration")
@ActiveProfiles("integration")
class FiTrackerApplicationTests extends AbstractIntegrationTest {
	@Test
	void contextLoads() {

	}

}
