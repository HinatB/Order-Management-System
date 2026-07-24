package com.wqlearn.oms.payment.event;

import com.wqlearn.oms.payment.domain.PaymentEntity;
import com.wqlearn.oms.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedEventConsumer.class);

    private final PaymentService paymentService;

    public OrderCreatedEventConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "${oms.kafka.topics.order-created}")
    public void handle(OrderCreatedEvent event) {
        PaymentEntity payment = paymentService.createPendingPayment(event);
        log.info(
                "Created pending payment: paymentId={}, orderId={}, customerId={}, amount={}, status={}",
                payment.getId(),
                event.orderId(),
                event.customerId(),
                event.totalAmount(),
                payment.getStatus()
        );
    }
}
