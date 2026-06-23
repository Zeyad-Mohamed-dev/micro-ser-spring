package com.ecommerce.order.events;

import java.util.List;

public class OrderCreatedEvent {
    private String orderNumber;
    private String customerEmail;
    private List<OrderItemEvent> items;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(String orderNumber, String customerEmail, List<OrderItemEvent> items) {
        this.orderNumber = orderNumber;
        this.customerEmail = customerEmail;
        this.items = items;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public List<OrderItemEvent> getItems() {
        return items;
    }

    public void setItems(List<OrderItemEvent> items) {
        this.items = items;
    }
}
