package com.shopflow.commons.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProcessedEvent {

    private String paymentId;
    private String orderId;
    private String userId;
    private PaymentStatus status;
    private LocalDateTime processedAt;

    public enum PaymentStatus {
        APPROVED, REJECTED
    }
}
