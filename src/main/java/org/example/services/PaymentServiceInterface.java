package org.example.services;

public interface PaymentServiceInterface {
    String createCheckoutSession(String rentalId);

    void handleWebhook(String payload, String signature);
}
