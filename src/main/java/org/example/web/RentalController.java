package org.example.web;

import org.example.dto.RentalRequest;
import org.example.dto.RentalResponse;
import org.example.dto.UserRequest;
import org.example.models.Rental;
import org.example.models.User;
import org.example.services.RentalServiceInterface;
import org.example.services.UserServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {
    private final RentalServiceInterface rentalService;
    private final UserServiceInterface userService;
    public RentalController ( RentalServiceInterface rentalService, UserServiceInterface userService ) {
        this.rentalService = rentalService;
        this.userService = userService;
    }
    @GetMapping
    public ResponseEntity<List<RentalResponse>> list() {
        List<RentalResponse> response = rentalService.findAllRentals().stream()
                .map(RentalResponse::fromEntity)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RentalResponse>> userRentals(@PathVariable("userId") String userId) {

        List<Rental> rentals = rentalService.findUserRentals(userId);

        List<RentalResponse> response = rentals.stream()
                .map(RentalResponse::fromEntity)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/me")
    public ResponseEntity<List<RentalResponse>> getMyRentals(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByLogin(userDetails.getUsername());

        List<RentalResponse> response = rentalService.findUserRentals(user.getId()).stream()
                .map(RentalResponse::fromEntity)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PostMapping("/rent")
    public ResponseEntity<Rental> rent(
            @RequestBody RentalRequest rentalRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String login = userDetails.getUsername();
        User user = userService.findByLogin(login);
        Rental rental = rentalService.rentVehicle(
                user.getId(),
                rentalRequest.vehicleId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }
    @PostMapping("/return")
    public ResponseEntity<Rental> returnVehicle(
            @RequestBody RentalRequest rentalRequest,
            @AuthenticationPrincipal UserDetails userDetails){
        String login = userDetails.getUsername();
        User user = userService.findByLogin(login);
        Rental rental = rentalService.returnVehicle(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(rental);
    }
}
