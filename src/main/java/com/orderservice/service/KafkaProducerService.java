package com.orderservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KafkaProducerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(String topic, String message, Object data) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("message", message);
            payload.put("data", data);
            String jsonPayload = objectMapper.writeValueAsString(payload);
            logger.info("Sending message to topic {}: {}", topic, jsonPayload);
            kafkaTemplate.send(topic, jsonPayload);
        } catch (Exception ex) {
            logger.error("Failed to send message to topic {}: {}", topic, ex.getMessage(), ex);
        }
    }
}
