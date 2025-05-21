package com.orderservice.controller;

import com.orderservice.dto.OrderDTO;
import com.orderservice.dto.PageResponse;
import com.orderservice.exception.InvalidInputException;
import com.orderservice.service.OrderService;
import com.orderservice.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private static final int MAX_PAGE_SIZE = 100;

    OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        String greeting = "Hello World!";
        logger.info("GET /test called and Test Connection Successful");
        return CommonUtils.getResponseEntity(greeting, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO orderDTO) {
        logger.info("POST /orders/create called with payload: {}", orderDTO);
        orderService.createOrder(orderDTO);
        logger.info("order: {} created", orderDTO.getOrderId());
        return CommonUtils.getResponseEntity("Order created successfully", HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable int orderId) {
        if (orderId <= 0) {
            throw new InvalidInputException("Order ID must be greater than 0");
        }
        logger.info("get /orders/{orderId} called with orderId: {}", orderId);
        OrderDTO orderByOrderId = orderService.getOrderByOrderId(orderId);
        logger.info("Order found with orderId: {}", orderId);
        return CommonUtils.getResponseEntity(orderByOrderId, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        if (page < 0 || size <= 0 || size > MAX_PAGE_SIZE) {
            throw new InvalidInputException("Invalid pagination parameters: page must be >= 0 and size must be between 0 and " + MAX_PAGE_SIZE);
        }
        logger.info("GET /orders called with page: {} and size: {}", page, size);
        PageResponse<OrderDTO> orders = orderService.getAllOrders(page, size);
        logger.info("Orders found with page: {} and size: {}", page, size);
        return CommonUtils.getResponseEntity(orders, HttpStatus.OK);
    }

}
