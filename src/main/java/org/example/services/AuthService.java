package org.example.services;

import org.example.models.User;
import org.example.repositories.UserRepository;

import java.util.Optional;

public class AuthService {
    UserRepository userRepo;

    public AuthService ( UserRepository userRepo ) {
        this.userRepo = userRepo;
    }

    boolean register(String login, String password){
        return false;
    }

    Optional<User> login( String login , String password){
        return null;
    }
}
