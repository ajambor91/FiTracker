package aj.FiTracker.FiTrackerExpenses.Services;


import aj.FiTracker.FiTrackerExpenses.Enums.KafkaAction;
import aj.FiTracker.FiTrackerExpenses.Models.MemberTemplate;
import aj.FiTracker.FiTrackerExpenses.Models.MembersTemplate;
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

    @BeforeEach
    public void start() {
        this.objectMapper = new ObjectMapper();
        this.membersServiceMock = mock(MembersService.class);
        this.kafkaConsumerService = new KafkaConsumerService(objectMapper, membersServiceMock);
    }

    @Test
    @DisplayName("Listen for zone members, add members")
    public void testListenForMembersAdd() throws JsonProcessingException {
        ConsumerRecord<String, String> consumerRecordMock = this.createAuthMock(
                KafkaAction.ADD_MEMBER,
                this.objectMapper.writeValueAsString(TestUtils.createMemberTemplateOneMember())
        );

        this.kafkaConsumerService.listenForZoneMembers(consumerRecordMock);
        ArgumentCaptor<MembersTemplate> argumentCaptor = ArgumentCaptor.forClass(MembersTemplate.class);
        verify(this.membersServiceMock, times(1)).addNewMembers(argumentCaptor.capture());
        MembersTemplate membersTemplate = argumentCaptor.getValue();
        assertEquals(1, membersTemplate.getMembersList().size());
        MembersTemplate.Member member = membersTemplate.getMembersList().getFirst();
        assertEquals(MEMBER_TEST_ID, member.memberId());
        assertEquals(ZONE_TEST_ID, membersTemplate.getZoneId());
    }

    @Test
    @DisplayName("Listen for zone members, remove members")
    public void testListenForMembersRemove() throws JsonProcessingException {

        ConsumerRecord<String, String> consumerRecordMock = this.createAuthMock(
                KafkaAction.REMOVE_MEMBER,
                this.objectMapper.writeValueAsString(TestUtils.createMemberTemplateOneMember())
        );

        this.kafkaConsumerService.listenForZoneMembers(consumerRecordMock);
        ArgumentCaptor<MembersTemplate> argumentCaptor = ArgumentCaptor.forClass(MembersTemplate.class);
        verify(this.membersServiceMock, times(1)).removeMembers(argumentCaptor.capture());
        MembersTemplate membersTemplate = argumentCaptor.getValue();
        assertEquals(1, membersTemplate.getMembersList().size());
        MembersTemplate.Member member = membersTemplate.getMembersList().getFirst();
        assertEquals(MEMBER_TEST_ID, member.memberId());
        assertEquals(ZONE_TEST_ID, membersTemplate.getZoneId());
    }

    @Test
    @DisplayName("Listen for zone members, unknown")
    public void testListenForMembersUnknown() throws JsonProcessingException {
        ConsumerRecord<String, String> consumerRecordMock = this.createAuthMock("UNKONW ACTION", "MESSAGE");

        this.kafkaConsumerService.listenForZoneMembers(consumerRecordMock);
        verify(this.membersServiceMock, never()).removeMembers(any(MembersTemplate.class));
        verify(this.membersServiceMock, never()).addNewMembers(any(MembersTemplate.class));
    }












    @Test
    @DisplayName("Listen for  member, remove members")
    public void testlistenForMemberRemove() throws JsonProcessingException {
        ConsumerRecord<String, String> consumerRecordMock = this.createAuthMock(KafkaAction.REMOVE_MEMBER,
                this.objectMapper.writeValueAsString(new MemberTemplate(MEMBER_TEST_ID))
                );
        this.kafkaConsumerService.listenForZoneMembers(consumerRecordMock);
        ArgumentCaptor<MembersTemplate> argumentCaptor = ArgumentCaptor.forClass(MembersTemplate.class);
        verify(this.membersServiceMock, times(1)).removeMembers(argumentCaptor.capture());
        MembersTemplate membersTemplate = argumentCaptor.getValue();
        assertEquals(1, membersTemplate.getMembersList().size());
        MembersTemplate.Member member = membersTemplate.getMembersList().getFirst();
        assertEquals(MEMBER_TEST_ID, member.memberId());
        assertEquals(ZONE_TEST_ID, membersTemplate.getZoneId());
    }

    @Test
    @DisplayName("Listen for member, unknown")
    public void testlistenForMemberUnknown() throws JsonProcessingException {
        ConsumerRecord<String, String> consumerRecordMock = this.createAuthMock("UNKONW ACTION", "MESSAGE");
        this.kafkaConsumerService.listenForMember(consumerRecordMock);
        verify(this.membersServiceMock, never()).removeMembers(any(MembersTemplate.class));
        verify(this.membersServiceMock, never()).addNewMembers(any(MembersTemplate.class));
    }

    private ConsumerRecord<String, String> createAuthMock(KafkaAction kafkaAction, String messageValue) throws JsonProcessingException {
        return this.createAuthMock(kafkaAction.getAction(), messageValue);
    }

    private ConsumerRecord<String, String> createAuthMock(String kafkaActionString, String messageValue) throws JsonProcessingException {
        Header headerMock = mock(Header.class);
        Headers headersMock = mock(Headers.class);
        ConsumerRecord<String, String> consumerRecordMock = mock(ConsumerRecord.class);
        when(headerMock.value()).thenReturn(kafkaActionString.getBytes(StandardCharsets.UTF_8));
        when(headersMock.lastHeader(eq("type"))).thenReturn(headerMock);
        when(consumerRecordMock.headers()).thenReturn(headersMock);
        when(consumerRecordMock.value()).thenReturn(this.objectMapper.writeValueAsString(TestUtils.createMemberTemplateOneMember()));
        return consumerRecordMock;
    }
}
