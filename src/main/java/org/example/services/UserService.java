package org.example.services;

import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.UserRepository;
import org.example.services.hibernate.RentalServiceInterface;
import org.example.services.hibernate.UserServiceInterface;

import java.util.List;

public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;
    private final RentalServiceInterface rentalService;

    public UserService(UserRepository userRepository, RentalServiceInterface rentalService) {
        this.userRepository = userRepository;
        this.rentalService = rentalService;
    }

    public void deleteUser(String userId, String loggedUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika."));

        if (userId.equals(loggedUserId)){
            throw new IllegalArgumentException("Nie można usunąć aktualnie zalogowanego użytkownika.");
        }

        boolean rental = rentalService.findActiveRentalByUserId(userId).isEmpty();
        if (!rental) {
            throw new IllegalArgumentException("Nie można usunć użytkownika, bo ma aktualnie wypożyczony pojazd.");
        }

        userRepository.deleteById(user.getId());
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findById(String id) {
        return userRepository.findById(id).orElseThrow();
    }
}
