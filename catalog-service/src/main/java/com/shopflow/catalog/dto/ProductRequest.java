package com.shopflow.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    private String description;

    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser maior que zero")
    private BigDecimal price;

    @NotNull(message = "Quantidade em estoque é obrigatória")
    private Integer stockQuantity;

    @NotBlank(message = "Categoria é obrigatória")
    private String category;

    private List<String> tags;

}
