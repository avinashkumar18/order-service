package com.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class OrderDTO {

    @NotNull(message = "OrderId is required")
    private Integer orderId;

    @NotBlank(message = "Item is required")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Item must be alphanumeric and can include spaces")
    private String item;

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    public OrderDTO() {
    }
    public OrderDTO(int orderId, String item, int quantity) {
        this.orderId = orderId;
        this.item = item;
        this.quantity = quantity;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
