package com.shopflow.stock.controller;

import com.shopflow.stock.dto.InventoryResponse;
import com.shopflow.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> findByProductId(
            @PathVariable String productId) {
        InventoryResponse response = stockService.findByProductId(productId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{productId}/add")
    public ResponseEntity<InventoryResponse> addStock(
            @PathVariable String productId,
            @RequestParam Integer quantity) {
        InventoryResponse response = stockService.addStock(productId, quantity);
        return ResponseEntity.ok(response);
    }
}