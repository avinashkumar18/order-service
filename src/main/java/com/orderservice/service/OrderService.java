package com.orderservice.service;

import com.orderservice.dto.OrderDTO;
import com.orderservice.dto.PageResponse;
import com.orderservice.exception.InvalidInputException;
import com.orderservice.exception.KafkaPublishException;
import com.orderservice.exception.ResourceNotFoundException;
import com.orderservice.mapper.OrderMapper;
import com.orderservice.model.Order;
import com.orderservice.repository.OrdersRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private static final String TOPIC = "orders";

    private final OrdersRepository ordersRepository;
    private final KafkaProducerService kafkaProducer;

    @Autowired
    public OrderService(OrdersRepository ordersRepository, KafkaProducerService kafkaProducer) {
        this.ordersRepository = ordersRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public void createOrder(OrderDTO orderDTO, boolean publishToKafka) {
        if (orderDTO == null || orderDTO.getOrderId() <= 0) {
            throw new InvalidInputException("Invalid order data: Order ID must be greater than 0");
        }
        if (isOrderExists(orderDTO.getOrderId())) {
            throw new InvalidInputException("Order with id: " + orderDTO.getOrderId() + " already exists");
        }
        logger.info("Creating order: {}", orderDTO);
        Order order = OrderMapper.INSTANCE.orderDTOToOrder(orderDTO);
        ordersRepository.save(order);
        logger.info("Order created successfully: {}", orderDTO);
        if (publishToKafka) {
            try {
                kafkaProducer.sendMessage(TOPIC, "Order created successfully", orderDTO);
            } catch (Exception ex) {
                logger.error("Failed to publish order to Kafka: {}", ex.getMessage(), ex);
                throw new KafkaPublishException("Failed to publish order to Kafka", ex);
            }
        }
    }

    public void createOrder(OrderDTO orderDTO) {
        createOrder(orderDTO, true);
    }

    public PageResponse<OrderDTO> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        logger.info("Getting all orders with page: {} and size: {}", page, size);
        Page<Order> orderPage = ordersRepository.findAll(pageable);
        List<OrderDTO> orderDTOList = CollectionUtils.isEmpty(orderPage.getContent()) ? new ArrayList<>()
                : orderPage.getContent().stream().map(OrderMapper.INSTANCE::orderToOrderDTO).toList(); //filter(o -> o.getQuantity() > 5).
        return new PageResponse<>(orderPage.getNumber(), orderPage.getSize(), orderPage.getTotalElements(), orderPage.getTotalPages(), orderDTOList);
    }

    public OrderDTO getOrderByOrderId(int orderId) {
        logger.info("Getting order by orderId: {}", orderId);
        Order orderByOrderId = ordersRepository.getOrderByOrderId(orderId);
        if (orderByOrderId == null) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }
        return OrderMapper.INSTANCE.orderToOrderDTO(orderByOrderId);
    }

    public boolean isOrderExists(int orderId) {
        return ordersRepository.getOrderByOrderId(orderId) != null;
    }
}
