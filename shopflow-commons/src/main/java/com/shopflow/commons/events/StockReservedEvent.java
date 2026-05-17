package com.shopflow.commons.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservedEvent {

    private String orderId;
    private List<StockItemEvent> items;
    private ReservationStatus status;
    private LocalDateTime processedAt;

    public enum ReservationStatus {
        RESERVED, FAILED
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockItemEvent {
        private String productId;
        private Integer quantity;
    }
}