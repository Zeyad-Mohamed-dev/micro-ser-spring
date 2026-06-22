package com.ecommerce.inventory.service;

import com.ecommerce.inventory.dto.InventoryRequest;
import com.ecommerce.inventory.dto.InventoryResponse;
import com.ecommerce.inventory.model.Inventory;
import com.ecommerce.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public InventoryResponse addStock(InventoryRequest request) {
        Inventory inventory = inventoryRepository.findBySku(request.getSku())
                .orElse(new Inventory(request.getSku(), 0));
        inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
        Inventory saved = inventoryRepository.save(inventory);
        return toResponse(saved);
    }

    public boolean isInStock(String sku, int quantity) {
        return inventoryRepository.findBySku(sku)
                .map(inv -> inv.getQuantity() >= quantity)
                .orElse(false);
    }

    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public InventoryResponse getBySku(String sku) {
        Inventory inventory = inventoryRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Inventory not found for sku: " + sku));
        return toResponse(inventory);
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getSku(),
                inventory.getQuantity()
        );
    }
}
