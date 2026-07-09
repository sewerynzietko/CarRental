package org.example.services.impl;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.example.models.Payment;
import org.example.models.PaymentStatus;
import org.example.models.Rental;
import org.example.repositories.RentalRepository;
import org.example.services.PaymentRepository;
import org.example.services.PaymentServiceInterface;
import org.example.services.VehicleLocationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class PaymentService implements PaymentServiceInterface {

    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final VehicleLocationService vehicleLocationService;

    @Value("${stripe.api-key}")
    private String apiKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    public PaymentService(RentalRepository rentalRepository, PaymentRepository paymentRepository, VehicleLocationService vehicleLocationService) {
        this.rentalRepository = rentalRepository;
        this.paymentRepository = paymentRepository;
        this.vehicleLocationService = vehicleLocationService;
    }

    @Override
    public String createCheckoutSession(String rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono wypożyczenia."));

        if (!rental.isActive()) {
            throw new IllegalStateException("To wypożyczenie zostało już zakończone.");
        }

        vehicleLocationService.validateReturnLocation(rental.getVehicle().getId());

        LocalDateTime start = rental.getRentDateTime();
        LocalDateTime end = LocalDateTime.now();

        long days = java.time.temporal.ChronoUnit.DAYS.between(start, end);
        if (start.plusDays(days).isBefore(end)) {
            days++;
        }

        if (days < 1) {
            days = 1;
        }
        double pricePerDay = rental.getVehicle().getPrice();
        long totalAmountInCents = Math.round(pricePerDay * days * 100);

        Stripe.apiKey = apiKey;

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName("Zwrot pojazdu: " + rental.getVehicle().getBrand() + " " + rental.getVehicle().getModel())
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("pln")
                        .setUnitAmount(totalAmountInCents)
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(priceData)
                        .build();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(successUrl)
                        .setCancelUrl(cancelUrl)
                        .addLineItem(lineItem)
                        .putMetadata("rentalId", rental.getId())
                        .build();

        try {
            Session session = Session.create(params);

            Payment payment = paymentRepository.findByRentalId(rental.getId())
                    .orElseGet(() -> Payment.builder()
                            .id(UUID.randomUUID().toString())
                            .rental(rental)
                            .createdAt(LocalDateTime.now())
                            .build());

            payment.setAmount(totalAmountInCents);
            payment.setCurrency("pln");
            payment.setStripeSessionId(session.getId());
            payment.setStatus(PaymentStatus.PENDING);
            paymentRepository.save(payment);

            return session.getUrl();
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas tworzenia sesji Stripe: " + e.getMessage());
        }
    }

    @Override
    public void handleWebhook(String payload, String sigHeader) {
        Stripe.apiKey = apiKey;
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new IllegalArgumentException("Błąd weryfikacji podpisu Webhooka!");
        }

        if ("checkout.session.completed".equals(event.getType())) {

            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            Session session;

            if (dataObjectDeserializer.getObject().isPresent()) {
                session = (Session) dataObjectDeserializer.getObject().get();
            } else {
                System.err.println("BŁĄD: Nie udało się zdeserializować sesji Stripe. Typ zdarzenia: " + event.getType());
                return;
            }

            String stripeSessionId = session.getId();

            paymentRepository.findByStripeSessionId(stripeSessionId)
                    .ifPresentOrElse(payment -> {
                        payment.setStatus(PaymentStatus.PAID);
                        paymentRepository.save(payment);

                        Rental rental = payment.getRental();
                        rental.setReturnDateTime(LocalDateTime.now());
                        rentalRepository.save(rental);

                        System.out.println("Płatność zaktualizowana pomyślnie dla sesji: " + stripeSessionId);
                    }, () -> System.err.println("Nie znaleziono płatności dla sesji: " + stripeSessionId));
        }
    }
}