package com.wqlearn.oms.order.event;

import com.wqlearn.oms.order.domain.OrderEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class KafkaOrderEventPublisherTest {

    @AfterEach
    void clearTransactionSynchronization() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void publishOrderCreatedSendsAfterTransactionCommit() {
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate = mock(KafkaTemplate.class);
        KafkaOrderEventPublisher publisher = new KafkaOrderEventPublisher(kafkaTemplate, "order.created");
        OrderEntity order = OrderEntity.create("customer-001", new BigDecimal("99.90"));

        TransactionSynchronizationManager.initSynchronization();

        publisher.publishOrderCreated(order);

        verify(kafkaTemplate, never()).send(any(), any(), any());

        List<TransactionSynchronization> synchronizations = TransactionSynchronizationManager.getSynchronizations();
        synchronizations.forEach(TransactionSynchronization::afterCommit);

        verify(kafkaTemplate).send(eq("order.created"), eq(order.getId().toString()), any(OrderCreatedEvent.class));
    }
}
