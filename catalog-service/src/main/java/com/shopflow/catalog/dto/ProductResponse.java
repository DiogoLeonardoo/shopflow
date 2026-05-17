package com.shopflow.catalog.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.shopflow.catalog.domain.Product;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductResponse {

    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String category;
    private List<String> tags;
    private boolean active;
    private LocalDateTime createdAt;

    public static ProductResponse from(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setCategory(product.getCategory());
        response.setTags(product.getTags());
        response.setActive(product.isActive());
        response.setCreatedAt(product.getCreatedAt());
        return response;
    }
}
