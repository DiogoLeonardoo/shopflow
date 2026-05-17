package com.shopflow.payment.service;

import com.shopflow.commons.events.OrderCreatedEvent;
import com.shopflow.commons.events.PaymentProcessedEvent;
import com.shopflow.payment.domain.IdempotencyKey;
import com.shopflow.payment.domain.Payment;
import com.shopflow.payment.dto.PaymentResponse;
import com.shopflow.payment.kafka.PaymentProducer;
import com.shopflow.payment.repository.IdempotencyKeyRepository;
import com.shopflow.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final PaymentProducer paymentProducer;

    @Transactional
    public void processPayment(OrderCreatedEvent event) {
        String idempotencyKey = "order-" + event.getOrderId();

        if (idempotencyKeyRepository.existsById(idempotencyKey)) {
            log.warn("Pagamento já processado para orderId: {}", event.getOrderId());
            return;
        }

        Payment payment = Payment.builder()
                .orderId(event.getOrderId())
                .userId(event.getUserId())
                .amount(event.getTotalAmount())
                .build();

        Payment saved = paymentRepository.save(payment);

        idempotencyKeyRepository.save(IdempotencyKey.builder()
                .key(idempotencyKey)
                .paymentId(saved.getId())
                .build());

        boolean approved = processWithGateway(event);

        if (approved) {
            saved.setStatus(Payment.PaymentStatus.APPROVED);
            saved.setProcessedAt(LocalDateTime.now());
            log.info("Pagamento aprovado para orderId: {}", event.getOrderId());
        } else {
            saved.setStatus(Payment.PaymentStatus.REJECTED);
            saved.setFailureReason("Pagamento recusado pelo gateway");
            saved.setProcessedAt(LocalDateTime.now());
            log.info("Pagamento rejeitado para orderId: {}", event.getOrderId());
        }

        paymentRepository.save(saved);

        publishPaymentProcessedEvent(saved);
    }

    public Optional<PaymentResponse> findByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(PaymentResponse::from);
    }

    private boolean processWithGateway(OrderCreatedEvent event) {
        log.info("Simulando gateway de pagamento para orderId: {}",
                event.getOrderId());
        return true;
    }

    private void publishPaymentProcessedEvent(Payment payment) {
        PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .status(payment.getStatus() == Payment.PaymentStatus.APPROVED
                        ? PaymentProcessedEvent.PaymentStatus.APPROVED
                        : PaymentProcessedEvent.PaymentStatus.REJECTED)
                .processedAt(payment.getProcessedAt())
                .build();

        paymentProducer.publishPaymentProcessed(event);
    }
}