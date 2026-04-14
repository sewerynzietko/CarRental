package org.example.services;

import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.UserRepository;

import java.util.List;

public class UserService {
    UserRepository userRepository;
    RentalService rentalService;
    public UserService(UserRepository userRepository, RentalService rentalService) {
        this.userRepository = userRepository;
        this.rentalService = rentalService;
    }

    public void deleteUser(String userId, String loggedUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika."));

        boolean rental = userRepository.equals("a");
        if (rental) {
            throw new IllegalArgumentException("Nie można usunć pojzdu, bo jest aktualnie wypożyczony.");
        }
        userRepository.deleteById(user.getId());
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findById(String id) {
        return null;
    }
}
