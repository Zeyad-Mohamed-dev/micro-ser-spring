package com.ecommerce.order.events;

public class OrderItemEvent {
    private String productSku;
    private Integer quantity;

    public OrderItemEvent() {
    }

    public OrderItemEvent(String productSku, Integer quantity) {
        this.productSku = productSku;
        this.quantity = quantity;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
