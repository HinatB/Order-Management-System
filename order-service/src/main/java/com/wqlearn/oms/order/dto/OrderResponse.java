package com.wqlearn.oms.order.dto;

import com.wqlearn.oms.order.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String customerId,
        BigDecimal totalAmount,
        OrderStatus status,
        Long version,
        Instant createdAt,
        Instant updatedAt
) {
}
