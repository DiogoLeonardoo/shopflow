package com.shopflow.order.repository;

import com.shopflow.order.domain.Order;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSpecification {

    public static Specification<Order> hasUserId(String userId) {
        return (root, query, cb) ->
                cb.equal(root.get("userId"), userId);
    }

    public static Specification<Order> hasId(String id) {
        return (root, query, cb) ->
                cb.equal(root.get("id"), id);
    }

    public static Specification<Order> hasStatus(String status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"),
                        Order.OrderStatus.valueOf(status));
    }

    public static Specification<Order> createdBetween(
            LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) ->
                cb.between(root.get("createdAt"), start, end);
    }
}
