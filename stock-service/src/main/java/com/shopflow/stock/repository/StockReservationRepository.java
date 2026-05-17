package com.shopflow.stock.repository;

import com.shopflow.stock.domain.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockReservationRepository extends JpaRepository<StockReservation, String> {
    List<StockReservation> findByOrderId(String orderId);
    List<StockReservation> findByProductId(String productId);
}