package com.wqlearn.oms.payment.service;

import com.wqlearn.oms.payment.domain.PaymentEntity;
import com.wqlearn.oms.payment.domain.PaymentStatus;
import com.wqlearn.oms.payment.event.OrderCreatedEvent;
import com.wqlearn.oms.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentServiceTest {

    @Test
    void createPendingPaymentSavesPaymentFromOrderCreatedEvent() {
        PaymentRepository paymentRepository = mock(PaymentRepository.class);
        PaymentService paymentService = new PaymentService(paymentRepository);
        UUID orderId = UUID.randomUUID();
        OrderCreatedEvent event = new OrderCreatedEvent(
                orderId,
                "customer-001",
                new BigDecimal("99.90"),
                "CREATED",
                Instant.parse("2026-07-24T01:00:00Z")
        );
        when(paymentRepository.save(any(PaymentEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaymentEntity payment = paymentService.createPendingPayment(event);

        assertThat(payment.getOrderId()).isEqualTo(orderId);
        assertThat(payment.getCustomerId()).isEqualTo("customer-001");
        assertThat(payment.getAmount()).isEqualByComparingTo("99.90");
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        verify(paymentRepository).save(any(PaymentEntity.class));
    }
}
