package com.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.dto.OrderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaConsumerService(OrderService orderService, ObjectMapper objectMapper) {
        this.orderService = orderService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "orders", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        try {
            logger.info("Received raw message from Kafka: {}", message);
            JsonNode root = objectMapper.readTree(message);
            String infoMessage = root.has("message") ? root.get("message").asText() : null;
            JsonNode dataNode = root.get("data");

            logger.info("Kafka message info: {}", infoMessage);

            if (dataNode != null && dataNode.isObject()) {
                OrderDTO orderDTO = objectMapper.treeToValue(dataNode, OrderDTO.class);
//                if (orderDTO == null || orderDTO.getOrderId() <= 0) {
//                    logger.warn("Invalid order data received from Kafka: {}", orderDTO);
//                    return;
//                }
                if (orderService.isOrderExists(orderDTO.getOrderId())) {
                    logger.warn("Order with id: {} already exists", orderDTO.getOrderId());
                    return;
                }
                orderService.createOrder(orderDTO, false);
                logger.info("Order processed and saved: {}", orderDTO);
            } else {
                logger.warn("No valid data found in Kafka message: {}", message);
            }
        }  catch (JsonProcessingException e) {
            logger.error("Failed to deserialize message: {}", message, e);
        }catch (Exception ex) {
            logger.error("Error processing Kafka message: {}", message, ex);
        }
    }
}
