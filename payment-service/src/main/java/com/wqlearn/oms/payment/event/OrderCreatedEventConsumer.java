package com.wqlearn.oms.payment.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedEventConsumer.class);

    @KafkaListener(topics = "${oms.kafka.topics.order-created}")
    public void handle(OrderCreatedEvent event) {
        log.info(
                "Received order.created event: orderId={}, customerId={}, totalAmount={}, status={}",
                event.orderId(),
                event.customerId(),
                event.totalAmount(),
                event.status()
        );
    }
}
