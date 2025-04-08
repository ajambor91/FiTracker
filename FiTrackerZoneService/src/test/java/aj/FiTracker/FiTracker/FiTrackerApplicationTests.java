package aj.FiTracker.FiTracker;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("integration")
@Tag("integration")
class FiTrackerApplicationTests {

    @Test
    void contextLoads() {
    }

}
