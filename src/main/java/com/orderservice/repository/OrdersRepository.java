package com.orderservice.repository;

import com.orderservice.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrdersRepository  extends MongoRepository<Order, String> {

    Order getOrderByOrderId(int orderId);
}
