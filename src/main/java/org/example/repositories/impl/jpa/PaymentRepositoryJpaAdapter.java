package org.example.repositories.impl.jpa;

import org.example.models.Payment;
import org.example.services.PaymentRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jpa")
public class PaymentRepositoryJpaAdapter implements PaymentRepository {
    private final PaymentJpaRepository delegate;

    public PaymentRepositoryJpaAdapter(PaymentJpaRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Payment> findAll() {
        return delegate.findAll();
    }

    @Override
    public Optional<Payment> findById(String id) {
        return delegate.findById(id);
    }

    @Override
    public Optional<Payment> findByStripeSessionId(String stripeSessionId) {
        return delegate.findByStripeSessionId(stripeSessionId);
    }

    @Override
    public Optional<Payment> findByRentalId(String rentalId) {
        return delegate.findByRental_Id(rentalId);
    }

    public Payment save(Payment payment) {
        if (payment.getId() == null ||
                payment.getId().isBlank()) {
            payment.setId(UUID.randomUUID().toString());
        }
        return delegate.save(payment);
    }
}