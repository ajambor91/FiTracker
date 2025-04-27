package aj.FiTracker.FiTracker.Services;


import aj.FiTracker.FiTracker.Enums.KafkaAction;
import aj.FiTracker.FiTracker.Models.MemberTemplate;
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
    private final ZoneService zoneService;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaConsumerService(
            ZoneService zoneService,
            ObjectMapper objectMapper
    ) {
        this.zoneService = zoneService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "request-members", groupId = "fit-zone-members-group")
    public void listenForMembers(ConsumerRecord<String, String> message) {
        KafkaAction action = this.extractTypHeader(message.headers());
        try {
            if (action == KafkaAction.REMOVE_MEMBER) {
                logger.debug("Processing message for action: REMOVE_MEMBER");
                MemberTemplate memberTemplate = this.objectMapper.readValue(message.value(), MemberTemplate.class);
                logger.debug("Message value successfully mapped to MembersTemplate: {}", memberTemplate);
                this.zoneService.removeMemberFromAllZone(memberTemplate);
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
