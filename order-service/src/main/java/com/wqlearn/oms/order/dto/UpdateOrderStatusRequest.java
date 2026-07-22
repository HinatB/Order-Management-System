package com.wqlearn.oms.order.dto;

import com.wqlearn.oms.order.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull(message = "status is required")
        OrderStatus status
) {
}
