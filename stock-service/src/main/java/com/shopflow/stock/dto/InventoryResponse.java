package com.shopflow.stock.dto;

import com.shopflow.stock.domain.Inventory;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryResponse {

    private String id;
    private String productId;
    private Integer quantityAvailable;
    private Integer quantityReserved;
    private Integer quantityTotal;
    private LocalDateTime updatedAt;

    public static InventoryResponse from(Inventory inventory) {
        InventoryResponse response = new InventoryResponse();
        response.setId(inventory.getId());
        response.setProductId(inventory.getProductId());
        response.setQuantityAvailable(inventory.getQuantityAvailable());
        response.setQuantityReserved(inventory.getQuantityReserved());
        response.setQuantityTotal(inventory.getQuantityTotal());
        response.setUpdatedAt(inventory.getUpdatedAt());
        return response;
    }
}