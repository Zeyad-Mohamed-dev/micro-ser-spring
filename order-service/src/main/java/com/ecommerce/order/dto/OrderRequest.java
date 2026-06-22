package com.ecommerce.order.dto;

import java.util.List;

public class OrderRequest {

    private String customerEmail;
    private List<OrderItemRequest> items;

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
}
