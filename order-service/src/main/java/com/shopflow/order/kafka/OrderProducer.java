package com.shopflow.order.kafka;

import com.shopflow.commons.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProducer {

    private static final String TOPIC = "order.created";

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public void publishOrderCreated(OrderCreatedEvent event) {
        log.info("Publicando evento order.created para orderId: {}", event.getOrderId());
        kafkaTemplate.send(TOPIC, event.getOrderId(), event);
    }
}
