package com.shopflow.payment.kafka;

import com.shopflow.commons.events.OrderCreatedEvent;
import com.shopflow.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "order.created", groupId = "payment-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Recebendo evento order.created para orderId: {}",
                event.getOrderId());
        paymentService.processPayment(event);
    }
}