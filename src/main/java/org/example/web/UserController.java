package org.example.web;

import org.example.dto.AddressRequest;
import org.example.dto.UserRequest;
import org.example.models.User;
import org.example.services.UserServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserServiceInterface userService;
    public UserController ( UserServiceInterface userService ) {
        this.userService = userService;
    }
    @GetMapping
    public List<User> list() {return userService.findAllUsers();}
    @GetMapping("/id")
    public ResponseEntity<User> get( @RequestBody UserRequest userRequest ) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findById(userRequest.userId()));
    }
    @PatchMapping("/me/address")
    public ResponseEntity<Void> updateAddress(
            @RequestBody AddressRequest addressRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        userService.updateAddress(userDetails.getUsername(), addressRequest);
        return ResponseEntity.ok().build();
    }
}
