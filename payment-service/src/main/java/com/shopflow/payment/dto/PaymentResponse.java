package com.shopflow.payment.dto;

import com.shopflow.payment.domain.Payment;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {

    private String id;
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private String status;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public static PaymentResponse from(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setOrderId(payment.getOrderId());
        response.setUserId(payment.getUserId());
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus().name());
        response.setFailureReason(payment.getFailureReason());
        response.setCreatedAt(payment.getCreatedAt());
        response.setProcessedAt(payment.getProcessedAt());
        return response;
    }
}