package org.example.services;

import org.example.models.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    List<Payment> findAll();
    Optional<Payment> findById(String id);
    Optional<Payment> findByStripeSessionId(
            String stripeSessionId);
    Optional<Payment> findByRentalId(
            String rentalId);
    Payment save(Payment payment);
}
