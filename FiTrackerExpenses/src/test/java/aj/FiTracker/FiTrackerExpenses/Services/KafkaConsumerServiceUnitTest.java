package aj.FiTracker.FiTrackerExpenses.Services;


import aj.FiTracker.FiTrackerExpenses.Enums.KafkaAction;
import aj.FiTracker.FiTrackerExpenses.Models.MemberTemplate;
import aj.FiTracker.FiTrackerExpenses.Utils.TestUtils;
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

import static aj.FiTracker.FiTrackerExpenses.Utils.TestUtils.MEMBER_TEST_ID;
import static aj.FiTracker.FiTrackerExpenses.Utils.TestUtils.ZONE_TEST_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("unit")
@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class KafkaConsumerServiceUnitTest {
    private ObjectMapper objectMapper;
    private MembersService membersServiceMock;
    private KafkaConsumerService kafkaConsumerService;
    private ConsumerRecord<String, String> consumerRecordMock;
    private Headers headersMock;
    private Header headerMock;

    @BeforeEach
    public void start() {
        this.objectMapper = new ObjectMapper();
        this.headerMock = mock(Header.class);
        this.headersMock = mock(Headers.class);
        this.membersServiceMock = mock(MembersService.class);
        this.consumerRecordMock = mock(ConsumerRecord.class);
        this.kafkaConsumerService = new KafkaConsumerService(objectMapper, membersServiceMock);
    }

    @Test
    @DisplayName("Listen for members, add members")
    public void testListenForMembersAdd() throws JsonProcessingException {
        when(this.headerMock.value()).thenReturn(KafkaAction.ADD_MEMBER.getAction().getBytes(StandardCharsets.UTF_8));
        when(this.headersMock.lastHeader(eq("type"))).thenReturn(this.headerMock);
        when(this.consumerRecordMock.headers()).thenReturn(this.headersMock);
        when(this.consumerRecordMock.value()).thenReturn(this.objectMapper.writeValueAsString(TestUtils.createMemberTemplateOneMember()));
        this.kafkaConsumerService.listenForMembers(this.consumerRecordMock);
        ArgumentCaptor<MemberTemplate> argumentCaptor = ArgumentCaptor.forClass(MemberTemplate.class);
        verify(this.membersServiceMock, times(1)).addNewMembers(argumentCaptor.capture());
        MemberTemplate memberTemplate = argumentCaptor.getValue();
        assertEquals(1, memberTemplate.getMembersList().size());
        MemberTemplate.Member member = memberTemplate.getMembersList().getFirst();
        assertEquals(MEMBER_TEST_ID, member.memberId());
        assertEquals(ZONE_TEST_ID, memberTemplate.getZoneId());
    }

    @Test
    @DisplayName("Listen for members, remove members")
    public void testListenForMembersRemove() throws JsonProcessingException {
        when(this.headerMock.value()).thenReturn(KafkaAction.REMOVE_MEMBER.getAction().getBytes(StandardCharsets.UTF_8));
        when(this.headersMock.lastHeader(eq("type"))).thenReturn(this.headerMock);
        when(this.consumerRecordMock.headers()).thenReturn(this.headersMock);
        when(this.consumerRecordMock.value()).thenReturn(this.objectMapper.writeValueAsString(TestUtils.createMemberTemplateOneMember()));
        this.kafkaConsumerService.listenForMembers(this.consumerRecordMock);
        ArgumentCaptor<MemberTemplate> argumentCaptor = ArgumentCaptor.forClass(MemberTemplate.class);
        verify(this.membersServiceMock, times(1)).removeMembers(argumentCaptor.capture());
        MemberTemplate memberTemplate = argumentCaptor.getValue();
        assertEquals(1, memberTemplate.getMembersList().size());
        MemberTemplate.Member member = memberTemplate.getMembersList().getFirst();
        assertEquals(MEMBER_TEST_ID, member.memberId());
        assertEquals(ZONE_TEST_ID, memberTemplate.getZoneId());
    }

    @Test
    @DisplayName("Listen for members, unknown")
    public void testListenForMembersUnknown() throws JsonProcessingException {
        when(this.headerMock.value()).thenReturn("UNKNOWN ACTION".getBytes(StandardCharsets.UTF_8));
        when(this.headersMock.lastHeader(eq("type"))).thenReturn(this.headerMock);
        when(this.consumerRecordMock.headers()).thenReturn(this.headersMock);
        when(this.consumerRecordMock.value()).thenReturn(this.objectMapper.writeValueAsString(TestUtils.createMemberTemplateOneMember()));
        this.kafkaConsumerService.listenForMembers(this.consumerRecordMock);
        verify(this.membersServiceMock, never()).removeMembers(any(MemberTemplate.class));
        verify(this.membersServiceMock, never()).addNewMembers(any(MemberTemplate.class));
    }
}
