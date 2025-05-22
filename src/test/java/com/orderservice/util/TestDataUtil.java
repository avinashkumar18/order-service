package com.orderservice.util;

import com.orderservice.dto.OrderDTO;
import com.orderservice.model.Order;

import java.util.ArrayList;
import java.util.List;

public class TestDataUtil {
    
    public static OrderDTO getSampleOrderDTO() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(111);
        orderDTO.setItem("iphone 14");
        orderDTO.setQuantity(3);
        return orderDTO;
    }

    public static Order getSampleOrder() {
        Order order = new Order();
        order.setOrderId(111);
        order.setItem("iphone 14");
        order.setQuantity(3);
        return order;
    }

    public static List<Order> getSampleOrderList() {
        List<Order> orders = new ArrayList<>();
        orders.add(getSampleOrder());
        return orders;
    }
}
