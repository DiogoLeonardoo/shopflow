package com.shopflow.catalog.dto;


import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductUpdateRequest {

    private String name;
    private String description;

    @Positive(message = "Preço deve ser maior que zero")
    private BigDecimal price;

    private Integer stockQuantity;
    private String category;
    private List<String> tags;
    private Boolean active;

}
