package com.ecommerce.inventory.dto;

public class InventoryRequest {

    private String sku;
    private Integer quantity;

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
