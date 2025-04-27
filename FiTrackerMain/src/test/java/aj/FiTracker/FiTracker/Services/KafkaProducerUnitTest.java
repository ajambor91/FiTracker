package aj.FiTracker.FiTracker.Services;

import aj.FiTracker.FiTracker.Models.MemberTemplate;
import aj.FiTracker.FiTracker.TestUtils.UserDataTestFactory;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@ActiveProfiles("tag")
public class KafkaProducerUnitTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private KafkaTemplate<String, String> kafkaTemplateMock;
    private KafkaProducerService kafkaProducerService;
    private MemberTemplate memberTemplate;
    @BeforeEach
    public void setup() {
        this.memberTemplate = UserDataTestFactory.createMemberTemplate();
        this.kafkaTemplateMock = mock(KafkaTemplate.class);
        this.kafkaProducerService = new KafkaProducerService(this.objectMapper, this.kafkaTemplateMock);
    }

    @Test
    @DisplayName("Should publish delete kafka message ")
    public void testSendDeletedMember() throws JsonProcessingException {
        this.kafkaProducerService.sendDeletedMember(this.memberTemplate);
        ArgumentCaptor<Message<String>> captor = ArgumentCaptor.forClass(Message.class);
        verify(this.kafkaTemplateMock).send(captor.capture());
        Message<String> message = captor.getValue();
        String currentMembers = this.objectMapper.writeValueAsString(this.memberTemplate);
        assertEquals(currentMembers, message.getPayload());
    }
}
