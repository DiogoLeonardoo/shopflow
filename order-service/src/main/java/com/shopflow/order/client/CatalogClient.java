package com.shopflow.order.client;

import com.shopflow.order.dto.ProductResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogClient {

    private final RestTemplate restTemplate;

    @Value("${services.catalog.url}")
    private String catalogUrl;

    @CircuitBreaker(name = "catalog-service", fallbackMethod = "fallbackGetProduct")
    @Retry(name = "catalog-service")
    public ProductResponse getProduct(String productId) {
        log.info("Consultando produto {} no catalog-service", productId);
        return restTemplate.getForObject(
                catalogUrl + "/products/" + productId,
                ProductResponse.class);
    }

    public ProductResponse fallbackGetProduct(String productId, Exception ex) {
        log.error("Catalog-service indisponivel para produto {}. Erro: {}",
                productId, ex.getMessage());
        throw new RuntimeException(
                "Serviço de catálogo indisponível. Tente novamente em instantes.");
    }
}