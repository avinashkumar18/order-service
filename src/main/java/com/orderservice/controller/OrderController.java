package com.orderservice.controller;

import com.orderservice.dto.OrderDTO;
import com.orderservice.dto.PageResponse;
import com.orderservice.exception.InvalidInputException;
import com.orderservice.exception.KafkaPublishException;
import com.orderservice.service.OrderService;
import com.orderservice.util.CommonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@Tag(name = "Order Controller", description = "APIs for managing orders")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private static final int MAX_PAGE_SIZE = 100;

    OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Test API connection")
    @ApiResponse(responseCode = "200", description = "Connection successful")
    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        String greeting = "Hello World!";
        logger.info("GET /test called and Test Connection Successful");
        return CommonUtils.getResponseEntity(greeting, HttpStatus.OK);
    }

    @Operation(summary = "Create a new order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Kafka publish failed")
    })
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody @Valid OrderDTO orderDTO) {
        logger.info("POST /orders/create called with payload: {}", orderDTO);
        try {
            orderService.createOrder(orderDTO);
            logger.info("order: {} created", orderDTO.getOrderId());
            return CommonUtils.getResponseEntity("Order created successfully", HttpStatus.CREATED);
        } catch (InvalidInputException ex) {
            logger.error("Invalid input: {}", ex.getMessage());
            return CommonUtils.getResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (KafkaPublishException ex) {
            logger.error("Kafka publish failed: {}", ex.getMessage());
            return CommonUtils.getResponseEntity("Order created but failed to publish to Kafka", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get order by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order found"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "400", description = "Invalid order ID")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@Parameter(description = "ID of the order", required = true) @PathVariable int orderId) {
        if (orderId <= 0) {
            throw new InvalidInputException("Order ID must be greater than 0");
        }
        logger.info("get /orders/{orderId} called with orderId: {}", orderId);
        OrderDTO orderByOrderId = orderService.getOrderByOrderId(orderId);
        logger.info("Order found with orderId: {}", orderId);
        return CommonUtils.getResponseEntity(orderByOrderId, HttpStatus.OK);
    }

    @Operation(summary = "Get all orders with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    @GetMapping
    public ResponseEntity<?> getAllOrders(@Parameter(description = "Page number (from 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") int size) {
        if (page < 0 || size <= 0 || size > MAX_PAGE_SIZE) {
            throw new InvalidInputException("Invalid pagination parameters: page must be >= 0 and size must be between 0 and " + MAX_PAGE_SIZE);
        }
        logger.info("GET /orders called with page: {} and size: {}", page, size);
        PageResponse<OrderDTO> orders = orderService.getAllOrders(page, size);
        logger.info("{} Orders found with page: {} and page size: {}", orders.getSize(), page, size);
        return CommonUtils.getResponseEntity(orders, HttpStatus.OK);
    }

}
