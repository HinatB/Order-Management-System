package com.wqlearn.oms.order.repository;

import com.wqlearn.oms.order.domain.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
}
