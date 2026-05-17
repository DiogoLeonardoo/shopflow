package com.shopflow.stock.jms;

import com.shopflow.stock.dto.StockReservationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockReservationSender {

    private final JmsTemplate jmsTemplate;

    public void send(StockReservationRequest request) {
        String queue = "stock.reservation." + request.getProductId();
        log.info("Enviando reserva para fila {}: orderId={} quantity={}",
                queue, request.getOrderId(), request.getQuantity());
        jmsTemplate.convertAndSend(queue, request);
    }
}