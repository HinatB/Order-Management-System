package com.wqlearn.oms.payment.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class OrderCreatedEventConsumerTest {

    @Test
    void handleLogsOrderCreatedEvent(CapturedOutput output) {
        OrderCreatedEventConsumer consumer = new OrderCreatedEventConsumer();
        UUID orderId = UUID.randomUUID();
        OrderCreatedEvent event = new OrderCreatedEvent(
                orderId,
                "customer-001",
                new BigDecimal("99.90"),
                "CREATED",
                Instant.parse("2026-07-24T01:00:00Z")
        );

        consumer.handle(event);

        assertThat(output).contains("Received order.created event");
        assertThat(output).contains(orderId.toString());
        assertThat(output).contains("customer-001");
        assertThat(output).contains("99.90");
        assertThat(output).contains("CREATED");
    }
}
