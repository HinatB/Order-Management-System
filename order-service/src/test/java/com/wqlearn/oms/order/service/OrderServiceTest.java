package com.wqlearn.oms.order.service;

import com.wqlearn.oms.order.domain.OrderEntity;
import com.wqlearn.oms.order.domain.OrderStatus;
import com.wqlearn.oms.order.dto.CreateOrderRequest;
import com.wqlearn.oms.order.dto.OrderResponse;
import com.wqlearn.oms.order.exception.OrderNotFoundException;
import com.wqlearn.oms.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrderSavesNewOrderWithCreatedStatus() {
        when(orderRepository.save(any(OrderEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.createOrder(
                new CreateOrderRequest("customer-001", new BigDecimal("99.90"))
        );

        assertThat(response.id()).isNotNull();
        assertThat(response.customerId()).isEqualTo("customer-001");
        assertThat(response.totalAmount()).isEqualByComparingTo("99.90");
        assertThat(response.status()).isEqualTo(OrderStatus.CREATED);
        verify(orderRepository).save(any(OrderEntity.class));
    }

    @Test
    void getOrderThrowsWhenOrderDoesNotExist() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(orderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining(orderId.toString());
    }

    @Test
    void listOrdersByCustomerReturnsMatchingOrders() {
        OrderEntity firstOrder = OrderEntity.create("customer-001", new BigDecimal("99.90"));
        OrderEntity secondOrder = OrderEntity.create("customer-001", new BigDecimal("199.90"));
        when(orderRepository.findByCustomerIdOrderByCreatedAtDesc("customer-001"))
                .thenReturn(List.of(firstOrder, secondOrder));

        List<OrderResponse> responses = orderService.listOrdersByCustomer("customer-001");

        assertThat(responses)
                .hasSize(2)
                .extracting(OrderResponse::customerId)
                .containsOnly("customer-001");
        verify(orderRepository).findByCustomerIdOrderByCreatedAtDesc("customer-001");
    }

    @Test
    void updateStatusMarksOrderAsPaid() {
        OrderEntity order = OrderEntity.create("customer-001", new BigDecimal("99.90"));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        OrderResponse response = orderService.updateStatus(order.getId(), OrderStatus.PAID);

        assertThat(response.status()).isEqualTo(OrderStatus.PAID);
        verify(orderRepository).findById(order.getId());
    }

    @Test
    void updateStatusRejectsCancellingPaidOrder() {
        OrderEntity order = OrderEntity.create("customer-001", new BigDecimal("99.90"));
        order.markAsPaid();
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateStatus(order.getId(), OrderStatus.CANCELLED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Paid order cannot be cancelled");
    }

    @Test
    void updateStatusRejectsPayingCancelledOrder() {
        OrderEntity order = OrderEntity.create("customer-001", new BigDecimal("99.90"));
        order.cancel();
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateStatus(order.getId(), OrderStatus.PAID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cancelled order cannot be paid");
    }
}
