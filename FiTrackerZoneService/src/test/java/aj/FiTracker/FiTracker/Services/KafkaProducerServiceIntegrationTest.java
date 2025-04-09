package aj.FiTracker.FiTracker.Services;


import aj.FiTracker.FiTracker.AbstractTest.AbstractIntegrationTest;
import aj.FiTracker.FiTracker.Documents.Zone;
import aj.FiTracker.FiTracker.Enums.MemberRole;
import aj.FiTracker.FiTracker.Models.MemberTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;


import static aj.FiTracker.FiTracker.TestUtils.ZoneFactory.*;


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
    public void testSendNewMembers() throws JsonProcessingException  {
        MemberTemplate memberTemplate = new MemberTemplate(ZONE_TEST_ID);
        Zone.Member member = new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME);
        memberTemplate.addMember(member);
        this.kafkaProducerService.sendNewMembers(memberTemplate);
    }
}
