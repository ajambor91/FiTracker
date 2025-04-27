package aj.FiTracker.FiTracker.Services;

import aj.FiTracker.FiTracker.Enums.KafkaAction;
import aj.FiTracker.FiTracker.Interfaces.KafkaTemplateInterface;
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

    private final Logger logger;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducerService(
            ObjectMapper objectMapper,
            KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.logger = LoggerFactory.getLogger(KafkaProducerService.class);
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendDeletedMember(MemberTemplate memberTemplate) throws JsonProcessingException {
        logger.info("Sending deleted members message to Kafka.");
        logger.debug("Deleted members template: {}", memberTemplate);
        this.sendMessage(memberTemplate);
        logger.info("Successfully sent deleted members message to Kafka.");
    }

    private void sendMessage(KafkaTemplateInterface kafkaModelTemplate) throws JsonProcessingException {
        logger.info("Preparing to send a Kafka message with action: {}", KafkaAction.REMOVE_MEMBER);
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
                .setHeader(KafkaHeaders.TOPIC, "request-members")
                .setHeader("type", KafkaAction.REMOVE_MEMBER.getAction())
                .build();
        logger.debug("Kafka message created. Topic: {}, Type: {}", message.getHeaders().get(KafkaHeaders.TOPIC), message.getHeaders().get("type"));
        this.kafkaTemplate.send(message);
        logger.info("Message sent to Kafka topic: request-zone-members with type: {}", KafkaAction.REMOVE_MEMBER.getAction());
    }
}
