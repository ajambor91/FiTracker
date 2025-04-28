package aj.FiTracker.FiTrackerExpenses.Services;


import aj.FiTracker.FiTrackerExpenses.Enums.KafkaAction;
import aj.FiTracker.FiTrackerExpenses.Models.MemberTemplate;
import aj.FiTracker.FiTrackerExpenses.Models.MembersTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class KafkaConsumerService {
    private final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final MembersService membersService;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaConsumerService(ObjectMapper objectMapper, MembersService membersService) {
        logger.info("Initializing KafkaConsumerService.");
        this.objectMapper = objectMapper;
        this.membersService = membersService;
    }

    @KafkaListener(topics = "request-members", groupId = "fit-expenses-members-group")
    public void listenForMember(ConsumerRecord<String, String> message) {
        logger.info("Received Kafka message from topic '{}', partition {}, offset {}.",
                message.topic(), message.partition(), message.offset());
        logger.debug("Message headers: {}", message.headers());
        logger.debug("Message value: {}", message.value());

        KafkaAction action = this.extractTypHeader(message.headers());
        if (action == null) {
            logger.warn("Received message with no 'type' header. Skipping processing.");
            return;
        }
        try {
            if (action == KafkaAction.REMOVE_MEMBER) {
                MemberTemplate memberTemplate = this.objectMapper.readValue(message.value(), MemberTemplate.class);
                this.membersService.removeMember(memberTemplate);
            }

        } catch (JsonMappingException e) {
            logger.error("JsonMappingException occurred while processing Kafka message.", e);
            throw new RuntimeException("Failed to map Kafka message value", e);
        } catch (JsonProcessingException e) {
            logger.error("JsonProcessingException occurred while processing Kafka message.", e);
            throw new RuntimeException("Failed to process Kafka message JSON", e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred while processing Kafka message.", e);
            throw new RuntimeException("Error processing Kafka message", e);
        }
    }

    @KafkaListener(topics = "request-zone-members", groupId = "fit-expenses-zone-members-group")
    public void listenForZoneMembers(ConsumerRecord<String, String> message) {
        logger.info("Received Kafka message from topic '{}', partition {}, offset {}.",
                message.topic(), message.partition(), message.offset());
        logger.debug("Message headers: {}", message.headers());
        logger.debug("Message value: {}", message.value());

        KafkaAction action = this.extractTypHeader(message.headers());
        if (action == null) {
            logger.warn("Received message with no 'type' header. Skipping processing.");
            return;
        }
        logger.info("Extracted Kafka action: {}", action);

        try {
            if (action == KafkaAction.ADD_MEMBER) {
                logger.debug("Processing message for action: ADD_MEMBER");
                MembersTemplate membersTemplate = this.objectMapper.readValue(message.value(), MembersTemplate.class);
                logger.debug("Message value successfully mapped to MembersTemplate: {}", membersTemplate);
                this.membersService.addNewMembers(membersTemplate);
                logger.info("Successfully processed ADD_MEMBER message for zone ID: {}", membersTemplate.getZoneId());
            } else if (action == KafkaAction.REMOVE_MEMBER) {
                logger.debug("Processing message for action: REMOVE_MEMBER");
                MembersTemplate membersTemplate = this.objectMapper.readValue(message.value(), MembersTemplate.class);
                logger.debug("Message value successfully mapped to MembersTemplate: {}", membersTemplate);
                this.membersService.removeMembers(membersTemplate);
                logger.info("Successfully processed REMOVE_MEMBER message for zone ID: {}", membersTemplate.getZoneId());
            } else {
                logger.warn("Received Kafka message with unhandled action type: {}", action);
            }
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException occurred while processing Kafka message.", e);
            throw new RuntimeException("Failed to map Kafka message value", e);
        } catch (JsonProcessingException e) {
            logger.error("JsonProcessingException occurred while processing Kafka message.", e);
            throw new RuntimeException("Failed to process Kafka message JSON", e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred while processing Kafka message.", e);
            throw new RuntimeException("Error processing Kafka message", e);
        }
    }

    private KafkaAction extractTypHeader(Headers headers) {
        logger.debug("Extracting 'type' header from message headers.");
        Header typeHeader = headers.lastHeader("type");
        if (typeHeader == null) {
            logger.warn("'type' header not found in message headers.");
            return null;
        }
        String valueType = new String(typeHeader.value(), StandardCharsets.UTF_8);
        logger.debug("Extracted 'type' header value: {}", valueType);
        KafkaAction action = KafkaAction.setAction(valueType);
        logger.debug("Mapped header value '{}' to KafkaAction: {}", valueType, action);
        return action;
    }
}