package aj.FiTracker.FiTracker.Services;


import aj.FiTracker.FiTracker.Abstract.AbstractIntegrationTest;
import aj.FiTracker.FiTracker.Models.MemberTemplate;
import aj.FiTracker.FiTracker.TestUtils.UserDataTestFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles("integration")
@Tag("integration")
@SpringBootTest
public class KafkaProducerServiceIntegrationTest extends AbstractIntegrationTest {
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaProducerServiceIntegrationTest(KafkaProducerService kafkaProducerService, ObjectMapper objectMapper) {
        this.kafkaProducerService = kafkaProducerService;
        this.objectMapper = objectMapper;
    }

    @Test
    @DisplayName("Should send a member message")
    public void testSendNewMembers() throws JsonProcessingException {
        MemberTemplate membersTemplate = UserDataTestFactory.createMemberTemplate();
        this.kafkaProducerService.sendDeletedMember(membersTemplate);
    }
}
