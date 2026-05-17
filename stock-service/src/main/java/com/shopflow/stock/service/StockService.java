package com.shopflow.stock.service;

import com.shopflow.commons.events.StockReservedEvent;
import com.shopflow.stock.domain.Inventory;
import com.shopflow.stock.domain.StockReservation;
import com.shopflow.stock.dto.InventoryResponse;
import com.shopflow.stock.dto.StockReservationRequest;
import com.shopflow.stock.kafka.StockProducer;
import com.shopflow.stock.repository.InventoryRepository;
import com.shopflow.stock.repository.StockReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final InventoryRepository inventoryRepository;
    private final StockReservationRepository reservationRepository;
    private final StockProducer stockProducer;

    @Transactional
    public void reserve(StockReservationRequest request) {
        Inventory inventory = inventoryRepository
                .findByProductId(request.getProductId())
                .orElseThrow(() -> new RuntimeException(
                        "Produto não encontrado no estoque: " + request.getProductId()));

        int available = inventory.getQuantityAvailable() - inventory.getQuantityReserved();

        if (available < request.getQuantity()) {
            log.warn("Estoque insuficiente para produto {}: disponível={} solicitado={}",
                    request.getProductId(), available, request.getQuantity());

            publishStockReservedEvent(request.getOrderId(),
                    List.of(request), StockReservedEvent.ReservationStatus.FAILED);
            return;
        }

        inventory.setQuantityReserved(
                inventory.getQuantityReserved() + request.getQuantity());
        inventoryRepository.save(inventory);

        StockReservation reservation = StockReservation.builder()
                .orderId(request.getOrderId())
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .build();
        reservationRepository.save(reservation);

        log.info("Estoque reservado: produto={} quantidade={}",
                request.getProductId(), request.getQuantity());

        publishStockReservedEvent(request.getOrderId(),
                List.of(request), StockReservedEvent.ReservationStatus.RESERVED);
    }

    public InventoryResponse findByProductId(String productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado no estoque"));
        return InventoryResponse.from(inventory);
    }

    @Transactional
    public InventoryResponse addStock(String productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseGet(() -> Inventory.builder()
                        .productId(productId)
                        .quantityAvailable(0)
                        .quantityReserved(0)
                        .updatedAt(LocalDateTime.now())
                        .build());

        inventory.setQuantityAvailable(inventory.getQuantityAvailable() + quantity);
        inventory.setUpdatedAt(LocalDateTime.now());

        Inventory saved = inventoryRepository.save(inventory);
        log.info("Estoque adicionado: produto={} quantidade={}", productId, quantity);
        return InventoryResponse.from(saved);
    }

    private void publishStockReservedEvent(String orderId,
                                           List<StockReservationRequest> requests,
                                           StockReservedEvent.ReservationStatus status) {

        List<StockReservedEvent.StockItemEvent> items = requests.stream()
                .map(r -> StockReservedEvent.StockItemEvent.builder()
                        .productId(r.getProductId())
                        .quantity(r.getQuantity())
                        .build())
                .collect(Collectors.toList());

        StockReservedEvent event = StockReservedEvent.builder()
                .orderId(orderId)
                .items(items)
                .status(status)
                .processedAt(LocalDateTime.now())
                .build();

        stockProducer.publishStockReserved(event);
    }
}