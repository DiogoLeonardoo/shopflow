package com.shopflow.stock.kafka;

import com.shopflow.commons.events.StockReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockProducer {

    private static final String TOPIC = "stock.reserved";

    private final KafkaTemplate<String, StockReservedEvent> kafkaTemplate;

    public void publishStockReserved(StockReservedEvent event) {
        log.info("Publicando evento stock.reserved para orderId: {}",
                event.getOrderId());
        kafkaTemplate.send(TOPIC, event.getOrderId(), event);
    }
}