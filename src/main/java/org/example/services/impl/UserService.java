package org.example.services.impl;

import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.RoleRepository;
import org.example.repositories.UserRepository;
import org.example.services.RentalServiceInterface;
import org.example.services.UserServiceInterface;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RentalServiceInterface rentalService;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepo, RentalServiceInterface rentalService,
                       RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepo;
        this.rentalService = rentalService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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
    public void register(String login, String password) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Login nie może być pusty.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Hasło nie może być puste.");
        }
        if (userRepository.findByLogin(login).isPresent()) {
            throw new IllegalArgumentException("Użytkownik już istnieje.");
        }
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Brak ROLE_USER."));
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .login(login)
                .passwordHash(passwordEncoder.encode(password))
                .roles(Set.of(userRole))
                .build();
        userRepository.save(user);
    }
}
