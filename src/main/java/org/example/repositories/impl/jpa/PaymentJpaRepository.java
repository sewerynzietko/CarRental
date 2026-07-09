package org.example.repositories;

import org.example.models.Payment;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Profile("jpa")
public interface PaymentJpaRepository
        extends JpaRepository<Payment, String> {
    Optional<Payment> findByStripeSessionId(
            String stripeSessionId);
    Optional<Payment> findByRental_Id(
            String rentalId);
}
