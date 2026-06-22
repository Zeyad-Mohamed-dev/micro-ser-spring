package com.ecommerce.inventory.dto;

public class InventoryResponse {

    private Long id;
    private String sku;
    private Integer quantity;

    public InventoryResponse() {}

    public InventoryResponse(Long id, String sku, Integer quantity) {
        this.id = id;
        this.sku = sku;
        this.quantity = quantity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
