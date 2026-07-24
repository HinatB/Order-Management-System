package com.wqlearn.oms.order.event;

import com.wqlearn.oms.order.domain.OrderEntity;

public interface OrderEventPublisher {

    void publishOrderCreated(OrderEntity order);
}
