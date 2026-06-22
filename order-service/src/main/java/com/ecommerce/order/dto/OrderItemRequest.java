package com.ecommerce.order.dto;

import java.math.BigDecimal;

public class OrderItemRequest {

    private String productSku;
    private Integer quantity;
    private BigDecimal unitPrice;

    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
