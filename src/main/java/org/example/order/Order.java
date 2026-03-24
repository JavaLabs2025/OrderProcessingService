package org.example.order;

import org.example.catalog.Item;
import org.example.customer.User;

import java.util.List;

public class Order {
    private final String orderId;
    private final User user;
    private final List<Item> items;
    private double finalPrice;
    private OrderStatus status;

    public Order(String orderId, User user, List<Item> items) {
        this.orderId = orderId;
        this.user = user;
        this.items = items;
        this.status = OrderStatus.CREATED;
    }

    // Геттеры и сеттеры
    public String getOrderId() {
        return orderId;
    }

    public User getUser() {
        return user;
    }

    public List<Item> getItems() {
        return items;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}