package com.wqlearn.oms.payment.event;

import com.wqlearn.oms.payment.domain.PaymentEntity;
import com.wqlearn.oms.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
class OrderCreatedEventConsumerTest {

    @Test
    void handleCreatesPendingPaymentAndLogsResult(CapturedOutput output) {
        PaymentService paymentService = mock(PaymentService.class);
        OrderCreatedEventConsumer consumer = new OrderCreatedEventConsumer(paymentService);
        UUID orderId = UUID.randomUUID();
        OrderCreatedEvent event = new OrderCreatedEvent(
                orderId,
                "customer-001",
                new BigDecimal("99.90"),
                "CREATED",
                Instant.parse("2026-07-24T01:00:00Z")
        );
        PaymentEntity payment = PaymentEntity.createPending(
                orderId,
                "customer-001",
                new BigDecimal("99.90")
        );
        when(paymentService.createPendingPayment(event)).thenReturn(payment);

        consumer.handle(event);

        verify(paymentService).createPendingPayment(event);
        assertThat(output).contains("Created pending payment");
        assertThat(output).contains(orderId.toString());
        assertThat(output).contains("customer-001");
        assertThat(output).contains("99.90");
        assertThat(output).contains("PENDING");
    }
}
