package aj.FiTracker.FiTracker.AbstractTest;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public abstract class AbstractIntegrationTest {
    private static final MongoDBTestContainer mongoTestContainer;

    static {
        mongoTestContainer = MongoDBTestContainer.getInstance();
    }


    @DynamicPropertySource
    public static void registerProps(DynamicPropertyRegistry props) {
        MongoDBTestContainer.registerProperties(props);
    }
}
