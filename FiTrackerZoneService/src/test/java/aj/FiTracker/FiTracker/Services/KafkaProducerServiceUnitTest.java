package aj.FiTracker.FiTracker.Services;


import aj.FiTracker.FiTracker.Documents.Zone;
import aj.FiTracker.FiTracker.Enums.MemberRole;
import aj.FiTracker.FiTracker.Models.MembersTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;

import static aj.FiTracker.FiTracker.TestUtils.ZoneFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("unit")
@Tag("unit")
public class KafkaProducerServiceUnitTest {

    private KafkaProducerService kafkaProducerService;
    private KafkaTemplate<String, String> kafkaTemplateMock;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        this.objectMapper = new ObjectMapper();
        this.kafkaTemplateMock = mock(KafkaTemplate.class);
        this.kafkaProducerService = new KafkaProducerService(this.kafkaTemplateMock, this.objectMapper);
    }

    @Test
    @DisplayName("Should send a member message")
    public void testSendNewMembers() throws JsonProcessingException {
        MembersTemplate membersTemplate = new MembersTemplate(ZONE_TEST_ID);
        Zone.Member member = new Zone.Member(OWNER_TEST_ID, MemberRole.ADMIN, USER_TEST_NAME);
        membersTemplate.addMember(member);
        this.kafkaProducerService.sendNewMembers(membersTemplate);
        ArgumentCaptor<Message<String>> captor = ArgumentCaptor.forClass(Message.class);
        verify(this.kafkaTemplateMock).send(captor.capture());
        Message<String> message = captor.getValue();
        String currentMembers = this.objectMapper.writeValueAsString(membersTemplate);
        assertEquals(currentMembers, message.getPayload());
    }

}
