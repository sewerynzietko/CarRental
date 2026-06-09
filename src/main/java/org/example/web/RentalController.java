package org.example.web;

import org.example.dto.RentalRequest;
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
    public List<Rental> list(){
        return rentalService.findAllRentals();
    }
    @GetMapping("/user")
    public ResponseEntity<List<Rental>> userRentals( @RequestBody UserRequest userRequest ){
        return ResponseEntity.status(HttpStatus.OK).body(rentalService.findUserRentals(userRequest.userId()));
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
