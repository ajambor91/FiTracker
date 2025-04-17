package aj.FiTracker.FiTracker.Services;

import aj.FiTracker.FiTracker.Enums.KafkaAction;
import aj.FiTracker.FiTracker.Interfaces.KafkaModelTemplate;
import aj.FiTracker.FiTracker.Models.MemberTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducerService {
    private final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        logger.info("Initializing KafkaProducerService.");
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendNewMembers(MemberTemplate memberTemplate) throws JsonProcessingException {
        logger.info("Sending new members message to Kafka.");
        logger.debug("New members template: {}", memberTemplate);
        this.sendMessage(memberTemplate, KafkaAction.ADD_MEMBER);
        logger.info("Successfully sent new members message to Kafka.");
    }

    public void sendDeletedMembers(MemberTemplate memberTemplate) throws JsonProcessingException {
        logger.info("Sending deleted members message to Kafka.");
        logger.debug("Deleted members template: {}", memberTemplate);
        this.sendMessage(memberTemplate, KafkaAction.REMOVE_MEMBER);
        logger.info("Successfully sent deleted members message to Kafka.");
    }

    private void sendMessage(KafkaModelTemplate kafkaModelTemplate, KafkaAction kafkaAction) throws JsonProcessingException {
        logger.info("Preparing to send a Kafka message with action: {}", kafkaAction);
        logger.debug("Kafka model template: {}", kafkaModelTemplate);
        String parsedMessage;
        try {
            parsedMessage = this.objectMapper.writeValueAsString(kafkaModelTemplate);
            logger.debug("Parsed message for Kafka: {}", parsedMessage);
        } catch (JsonProcessingException e) {
            logger.error("Error while parsing message to JSON.", e);
            throw e;
        }
        Message<String> message = MessageBuilder
                .withPayload(parsedMessage)
                .setHeader(KafkaHeaders.TOPIC, "request-zone-members")
                .setHeader("type", kafkaAction.getAction())
                .build();
        logger.debug("Kafka message created. Topic: {}, Type: {}", message.getHeaders().get(KafkaHeaders.TOPIC), message.getHeaders().get("type"));
        this.kafkaTemplate.send(message);
        logger.info("Message sent to Kafka topic: request-zone-members with type: {}", kafkaAction.getAction());
    }
}