package com.wqlearn.oms.payment.service;

import com.wqlearn.oms.payment.domain.PaymentEntity;
import com.wqlearn.oms.payment.event.OrderCreatedEvent;
import com.wqlearn.oms.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentEntity createPendingPayment(OrderCreatedEvent event) {
        PaymentEntity payment = PaymentEntity.createPending(
                event.orderId(),
                event.customerId(),
                event.totalAmount()
        );
        return paymentRepository.save(payment);
    }
}
