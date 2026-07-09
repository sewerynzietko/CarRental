package org.example.web;

import org.example.services.PaymentServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentServiceInterface paymentService;

    public PaymentController(PaymentServiceInterface paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/checkout/{rentalId}")
    public ResponseEntity<Map<String, String>> createCheckoutSession(
            @PathVariable String rentalId) {
        String url = paymentService.createCheckoutSession(rentalId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("url", url));
    }
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature) {
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccess() {
        return ResponseEntity.ok("Płatność w toku. Oczekujemy na potwierdzenie od operatora.");
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> paymentCancel() {
        return ResponseEntity.ok("Płatność została anulowana.");
    }
}