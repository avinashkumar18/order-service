package com.orderservice.controller;

import com.orderservice.dto.OrderDTO;
import com.orderservice.dto.PageResponse;
import com.orderservice.exception.ResourceNotFoundException;
import com.orderservice.service.OrderService;
import com.orderservice.util.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@DisplayName("Order Controller Tests")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    private OrderDTO testOrderDTO;

    @BeforeEach
    void setUp() {
        testOrderDTO = TestDataUtil.getSampleOrderDTO();
    }

    @Nested
    @DisplayName("GET /orders/{orderId} Tests")
    class GetOrderByIdTests {
        @Test
        @DisplayName("Should return 200 when order exists")
        void getOrderById_ValidId_ReturnsOk() throws Exception {
            when(orderService.getOrderByOrderId(111)).thenReturn(testOrderDTO);

            mockMvc.perform(get("/orders/111")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("orderId").value(111));
        }

        @Test
        @DisplayName("Should return 404 when order not found")
        void getOrderById_NonExistentId_ReturnsNotFound() throws Exception {
            when(orderService.getOrderByOrderId(999))
                .thenThrow(new ResourceNotFoundException("Order not found"));

            mockMvc.perform(get("/orders/999")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    void testConnection_ReturnsOk() throws Exception {
        mockMvc.perform(get("/orders/test"))
                .andExpect(status().isOk());
    }

    @Test
    void createOrder_ValidOrder_ReturnsCreated() throws Exception {
        mockMvc.perform(post("/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":1,\"item\":\"iphone\",\"quantity\":2}"))
                .andExpect(status().isCreated());
    }

    @Test
    void getAllOrders_ValidParams_ReturnsOk() throws Exception {
        PageResponse<OrderDTO> pageResponse = new PageResponse<>(0, 1, 1L, 1,
                Collections.singletonList(TestDataUtil.getSampleOrderDTO()));
        when(orderService.getAllOrders(anyInt(), anyInt())).thenReturn(pageResponse);

        mockMvc.perform(get("/orders?page=0&size=10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 400 when page size exceeds maximum")
    void getAllOrders_ExceedsMaxPageSize_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/orders?page=0&size=101")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                    org.hamcrest.Matchers.containsString("Invalid pagination parameters")));
    }
}
