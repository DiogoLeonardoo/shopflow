package com.shopflow.stock.kafka;

import com.shopflow.commons.events.OrderCreatedEvent;
import com.shopflow.stock.dto.StockReservationRequest;
import com.shopflow.stock.jms.StockReservationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final StockReservationSender reservationSender;

    @KafkaListener(topics = "order.created", groupId = "stock-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Recebendo evento order.created para orderId: {}",
                event.getOrderId());

        event.getItems().forEach(item -> {
            StockReservationRequest request = StockReservationRequest.builder()
                    .orderId(event.getOrderId())
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .build();

            reservationSender.send(request);
        });
    }
}