package org.example.web;

import org.example.dto.UserRequest;
import org.example.dto.VehicleRequest;
import org.example.models.User;
import org.example.services.UserServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
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
}
