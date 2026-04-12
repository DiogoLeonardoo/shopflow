package com.shopflow.order.service;

import com.shopflow.commons.events.OrderCreatedEvent;
import com.shopflow.order.domain.Order;
import com.shopflow.order.domain.OrderItem;
import com.shopflow.order.dto.OrderRequest;
import com.shopflow.order.dto.OrderResponse;
import com.shopflow.order.kafka.OrderProducer;
import com.shopflow.order.repository.OrderRepository;
import com.shopflow.order.repository.OrderSpecification;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    @Transactional
    public OrderResponse create(OrderRequest request, String userId) {

        List<OrderItem> items = request.getItems().stream()
                .map(itemRequest -> OrderItem.builder()
                        .productId(itemRequest.getProductId())
                        .quantity(itemRequest.getQuantity())
                        .price(itemRequest.getPrice())
                        .build())
                .collect(Collectors.toList());

        BigDecimal totalAmount = items.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .userId(userId)
                .totalAmount(totalAmount)
                .items(items)
                .build();

        items.forEach(item -> item.setOrder(order));

        Order saved = orderRepository.save(order);

        publishOrderCreatedEvent(saved);

        log.info("Pedido criado com sucesso: {}", saved.getId());

        return OrderResponse.from(saved);
    }

    private void publishOrderCreatedEvent(Order order) {
        List<OrderCreatedEvent.OrderItemEvent> itemEvents = order.getItems().stream()
                .map(item -> OrderCreatedEvent.OrderItemEvent.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .items(itemEvents)
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .build();

        orderProducer.publishOrderCreated(event);
    }

    public List<OrderResponse> findOrders(
            String userId,
            String userRole,
            String orderId,
            String status,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        Specification<Order> spec = Specification.where(null);

        if (userRole.equals("CUSTOMER")) {
            spec = spec.and(OrderSpecification.hasUserId(userId));
        }

        if (orderId != null) {
            spec = spec.and(OrderSpecification.hasId(orderId));
        }

        if (status != null) {
            spec = spec.and(OrderSpecification.hasStatus(status));
        }

        if (startDate != null && endDate != null) {
            spec = spec.and(OrderSpecification.createdBetween(startDate, endDate));
        }

        return orderRepository.findAll(spec)
                .stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }
}
