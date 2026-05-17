package com.shopflow.payment.kafka;

import com.shopflow.commons.events.PaymentProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentProducer {

    private static final String TOPIC = "payment.processed";

    private final KafkaTemplate<String, PaymentProcessedEvent> kafkaTemplate;

    public void publishPaymentProcessed(PaymentProcessedEvent event) {
        log.info("Publicando evento payment.processed para orderId: {}",
                event.getOrderId());
        kafkaTemplate.send(TOPIC, event.getOrderId(), event);
    }
}