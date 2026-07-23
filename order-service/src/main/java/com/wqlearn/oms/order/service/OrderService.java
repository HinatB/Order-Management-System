package com.wqlearn.oms.order.service;

import com.wqlearn.oms.order.domain.OrderEntity;
import com.wqlearn.oms.order.domain.OrderStatus;
import com.wqlearn.oms.order.dto.CreateOrderRequest;
import com.wqlearn.oms.order.dto.OrderResponse;
import com.wqlearn.oms.order.exception.OrderNotFoundException;
import com.wqlearn.oms.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        OrderEntity order = OrderEntity.create(request.customerId(), request.totalAmount());
        return toResponse(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(this::toResponse)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public OrderResponse updateStatus(UUID orderId, OrderStatus targetStatus) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (targetStatus == OrderStatus.PAID) {
            order.markAsPaid();
        } else if (targetStatus == OrderStatus.CANCELLED) {
            order.cancel();
        } else if (targetStatus != OrderStatus.CREATED) {
            throw new IllegalArgumentException("Unsupported order status: " + targetStatus);
        }

        return toResponse(order);
    }

    private OrderResponse toResponse(OrderEntity order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getVersion(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
