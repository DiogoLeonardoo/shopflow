package com.shopflow.catalog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopflow.catalog.domain.Product;
import com.shopflow.catalog.dto.ProductRequest;
import com.shopflow.catalog.dto.ProductResponse;
import com.shopflow.catalog.dto.ProductUpdateRequest;
import com.shopflow.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_PREFIX = "product:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    public ProductResponse create(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .category(request.getCategory())
                .tags(request.getTags())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        Product saved = productRepository.save(product);
        log.info("Produto criado: {}", saved.getId());
        return ProductResponse.from(saved);
    }

    public ProductResponse findById(String id) {
        String cacheKey = CACHE_PREFIX + id;

        try {
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.info("Produto encontrado no cache: {}", id);
                return objectMapper.readValue(cached, ProductResponse.class);
            }
        } catch (Exception e) {
            log.warn("Erro ao ler cache: {}", e.getMessage());
        }

        log.info("Buscando produto no banco: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        ProductResponse response = ProductResponse.from(product);

        try {
            redisTemplate.opsForValue().set(cacheKey,
                    objectMapper.writeValueAsString(response), CACHE_TTL);
        } catch (Exception e) {
            log.warn("Erro ao salvar cache: {}", e.getMessage());
        }

        return response;
    }

    public List<ProductResponse> findAll(String category, String name) {
        List<Product> products;

        if (category != null) {
            products = productRepository.findByCategory(category);
        } else if (name != null) {
            products = productRepository.findByNameContainingIgnoreCase(name);
        } else {
            products = productRepository.findByActiveTrue();
        }

        return products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    public ProductResponse update(String id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getStockQuantity() != null) product.setStockQuantity(request.getStockQuantity());
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        if (request.getTags() != null) product.setTags(request.getTags());
        if (request.getActive() != null) product.setActive(request.getActive());
        product.setUpdatedAt(LocalDateTime.now());

        Product saved = productRepository.save(product);

        redisTemplate.delete(CACHE_PREFIX + id);
        log.info("Produto atualizado e cache invalidado: {}", id);

        return ProductResponse.from(saved);
    }

    public void delete(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        product.setActive(false);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);

        redisTemplate.delete(CACHE_PREFIX + id);
        log.info("Produto desativado e cache invalidado: {}", id);
    }
}