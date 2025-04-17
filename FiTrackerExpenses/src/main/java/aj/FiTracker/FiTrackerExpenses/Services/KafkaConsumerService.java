package aj.FiTracker.FiTrackerExpenses.Services;


import aj.FiTracker.FiTrackerExpenses.Enums.KafkaAction;
import aj.FiTracker.FiTrackerExpenses.Models.MemberTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class KafkaConsumerService {

    private final MembersService membersService;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaConsumerService(ObjectMapper objectMapper, MembersService membersService) {
        this.objectMapper = objectMapper;
        this.membersService = membersService;
    }

    @KafkaListener(topics = "request-zone-members", groupId = "fit-expenses-members-group")
    public void listenForMembers(ConsumerRecord<String, String> message) {
        KafkaAction action = this.extractTypHeader(message.headers());
        try {
            if (action == KafkaAction.ADD_MEMBER) {
                MemberTemplate memberTemplate = this.objectMapper.readValue(message.value(), MemberTemplate.class);
                this.membersService.addNewMembers(memberTemplate);
            }
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private KafkaAction extractTypHeader(Headers headers) {
        Header typeHeader = headers.lastHeader("type");
        if (typeHeader == null) {
            return null;
        }
        String valueType = new String(typeHeader.value(), StandardCharsets.UTF_8);
        return KafkaAction.setAction(valueType);
    }
}
