package com.wqlearn.oms.order.event;

import com.wqlearn.oms.order.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        String customerId,
        BigDecimal totalAmount,
        OrderStatus status,
        Instant createdAt
) {
}
