package com.shopflow.stock.jms;

import com.shopflow.stock.dto.StockReservationRequest;
import com.shopflow.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockReservationConsumer {

    private final StockService stockService;

    @JmsListener(destination = "stock.reservation.>",
            containerFactory = "jmsListenerContainerFactory")
    public void handleReservation(StockReservationRequest request) {
        log.info("Processando reserva: orderId={} productId={} quantity={}",
                request.getOrderId(),
                request.getProductId(),
                request.getQuantity());
        stockService.reserve(request);
    }
}