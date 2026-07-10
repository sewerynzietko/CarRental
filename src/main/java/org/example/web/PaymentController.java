package org.example.web;

import org.example.models.Rental;
import org.example.models.User;
import org.example.services.PaymentServiceInterface;
import org.example.services.RentalServiceInterface;
import org.example.services.UserServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentServiceInterface paymentService;
    private final UserServiceInterface userService;
    private final RentalServiceInterface rentalService;

    public PaymentController(PaymentServiceInterface paymentService, UserServiceInterface userService, RentalServiceInterface rentalService) {
        this.paymentService = paymentService;
        this.userService = userService;
        this.rentalService = rentalService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, String>> createCheckoutSession(
            @AuthenticationPrincipal UserDetails userDetails) {

        String login = userDetails.getUsername();
        User user = userService.findByLogin(login);

        Rental activeRental = rentalService.findActiveRentalByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Użytkownik nie ma aktywnego wypożyczenia do opłacenia."));

        String url = paymentService.createCheckoutSession(activeRental.getId());

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