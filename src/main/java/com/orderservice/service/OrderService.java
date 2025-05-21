package com.orderservice.service;

import com.orderservice.dto.OrderDTO;
import com.orderservice.dto.PageResponse;
import com.orderservice.exception.InvalidInputException;
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

    private final OrdersRepository ordersRepository;

    @Autowired
    public OrderService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    public void createOrder(OrderDTO orderDTO) {
        logger.info("Creating order: {}", orderDTO);
        if (orderDTO == null || orderDTO.getOrderId() <= 0) {
            throw new InvalidInputException("Invalid order data: Order ID must be greater than 0");
        }
        Order order = OrderMapper.INSTANCE.orderDTOToOrder(orderDTO);
        ordersRepository.save(order);
        logger.info("Order created successfully: {}", orderDTO);
    }

    public PageResponse<OrderDTO> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        logger.info("Getting all orders with page: {} and size: {}", page, size);
        Page<Order> orderPage = ordersRepository.findAll(pageable);
        List<OrderDTO> orderDTOList = CollectionUtils.isEmpty(orderPage.getContent()) ? new ArrayList<>()
                : orderPage.getContent().stream().map(OrderMapper.INSTANCE::orderToOrderDTO).toList();
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
}
