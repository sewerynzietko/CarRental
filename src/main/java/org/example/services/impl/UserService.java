package org.example.services.impl;

import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.UserRepository;
import org.example.services.UserServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;
    private final RentalService rentalService;
    public UserService(UserRepository userRepository, RentalService rentalService) {
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

    @Override
    public User findByLogin ( String login ) {
        return userRepository.findByLogin(login).orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika."));
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika."));
    }
}
