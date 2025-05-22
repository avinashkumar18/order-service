package com.orderservice.service;

import com.orderservice.dto.OrderDTO;
import com.orderservice.dto.PageResponse;
import com.orderservice.exception.InvalidInputException;
import com.orderservice.exception.ResourceNotFoundException;
import com.orderservice.model.Order;
import com.orderservice.repository.OrdersRepository;
import com.orderservice.util.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Service Tests")
class OrderServiceTest {

    @Mock
    private OrdersRepository ordersRepository;

    @InjectMocks
    private OrderService orderService;

    private OrderDTO testOrderDTO;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrderDTO = TestDataUtil.getSampleOrderDTO();
        testOrder = TestDataUtil.getSampleOrder();
    }

    @Nested
    @DisplayName("Create Order Tests")
    class CreateOrderTests {
        @Test
        @DisplayName("Should create order successfully when input is valid")
        void createOrder_ValidOrder_Success() {
            when(ordersRepository.save(any())).thenReturn(testOrder);

            assertDoesNotThrow(() -> orderService.createOrder(testOrderDTO, false));
            verify(ordersRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("Should throw exception when order is invalid")
        void createOrder_InvalidOrder_ThrowsException() {
            testOrderDTO.setOrderId(-1);

            assertThrows(InvalidInputException.class, () -> orderService.createOrder(testOrderDTO, false));
            verify(ordersRepository, never()).save(any());
        }
    }

    @Test
    void getOrderByOrderId_ValidId_ReturnsOrder() {
        when(ordersRepository.getOrderByOrderId(111)).thenReturn(TestDataUtil.getSampleOrder());

        OrderDTO result = orderService.getOrderByOrderId(111);
        assertNotNull(result);
        assertEquals(111, result.getOrderId());
    }

    @Test
    void getOrderByOrderId_InvalidId_ThrowsException() {
        when(ordersRepository.getOrderByOrderId(-1)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderByOrderId(-1));
    }

    @Test
    void getAllOrders_ValidParams_ReturnsPageResponse() {
        Page<Order> page = new PageImpl<>(TestDataUtil.getSampleOrderList());
        when(ordersRepository.findAll(any(PageRequest.class))).thenReturn(page);

        PageResponse<OrderDTO> result = orderService.getAllOrders(0, 10);
        assertNotNull(result);
        assertEquals(1, result.getData().size());
    }
}
