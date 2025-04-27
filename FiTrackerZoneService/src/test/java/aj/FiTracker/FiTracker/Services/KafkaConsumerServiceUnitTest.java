package aj.FiTracker.FiTracker.Services;

import aj.FiTracker.FiTracker.Enums.KafkaAction;
import aj.FiTracker.FiTracker.Models.MemberTemplate;
import aj.FiTracker.FiTracker.TestUtils.ZoneFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;

import static aj.FiTracker.FiTracker.TestUtils.ZoneFactory.MEMBER_TEST_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@ActiveProfiles("unit")
public class KafkaConsumerServiceUnitTest {
    private KafkaConsumerService kafkaConsumerService;
    private ConsumerRecord<String, String> consumerRecordMock;
    private Headers headersMock;
    private Header headerMock;
    private ZoneService zoneServiceMock;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {

        this.headerMock = mock(Header.class);
        this.headersMock = mock(Headers.class);
        this.consumerRecordMock = mock(ConsumerRecord.class);
        this.zoneServiceMock = mock(ZoneService.class);
        this.kafkaConsumerService = new KafkaConsumerService(this.zoneServiceMock, this.objectMapper);
    }

    @Test
    @DisplayName("Should handle member to delete")
    public void testListenForMembers() throws JsonProcessingException {
        when(this.headerMock.value()).thenReturn(KafkaAction.REMOVE_MEMBER.getAction().getBytes(StandardCharsets.UTF_8));
        when(this.headersMock.lastHeader(eq("type"))).thenReturn(this.headerMock);
        when(this.consumerRecordMock.headers()).thenReturn(this.headersMock);
        when(this.consumerRecordMock.value()).thenReturn(this.objectMapper.writeValueAsString(ZoneFactory.createMemberTemplate()));
        this.kafkaConsumerService.listenForMembers(this.consumerRecordMock);
        ArgumentCaptor<MemberTemplate> argumentCaptor = ArgumentCaptor.forClass(MemberTemplate.class);
        verify(this.zoneServiceMock, times(1)).removeMemberFromAllZone(argumentCaptor.capture());
        MemberTemplate memberTemplate = argumentCaptor.getValue();
        assertEquals(MEMBER_TEST_ID, memberTemplate.userId());

    }

    @Test
    @DisplayName("Should do nothing when message type is unknown")
    public void testListenForMembersUnknownType() throws JsonProcessingException {
        when(this.headerMock.value()).thenReturn("UNKNOWN".getBytes(StandardCharsets.UTF_8));
        when(this.headersMock.lastHeader(eq("type"))).thenReturn(this.headerMock);
        when(this.consumerRecordMock.headers()).thenReturn(this.headersMock);
        this.kafkaConsumerService.listenForMembers(this.consumerRecordMock);
        verify(this.zoneServiceMock, never()).removeMemberFromAllZone(any(MemberTemplate.class));
    }
}
