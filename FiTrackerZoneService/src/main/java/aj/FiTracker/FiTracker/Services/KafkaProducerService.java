package aj.FiTracker.FiTracker.Services;

import aj.FiTracker.FiTracker.Enums.KafkaAction;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

import aj.FiTracker.FiTracker.Interfaces.KafkaModelTemplate;
import aj.FiTracker.FiTracker.Models.MemberTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendNewMember(MemberTemplate memberTemplate) {

    }

    private void sendMessage(KafkaModelTemplate kafkaModelTemplate, KafkaAction kafkaAction) throws JsonProcessingException {
        String parsedMessage = this.objectMapper.writeValueAsString(kafkaModelTemplate);
        Message<String> message = MessageBuilder
                .withPayload(parsedMessage)
                .setHeader(KafkaHeaders.TOPIC, "request-zone-members")
                .setHeader("type", kafkaAction.getAction())
                .build();
        this.kafkaTemplate.send(message);
    }
}
