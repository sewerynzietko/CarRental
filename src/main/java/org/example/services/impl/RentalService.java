package org.example.services.impl;

import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleRepository;
import org.example.services.RentalServiceInterface;
import org.example.services.VehicleLocationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RentalService implements RentalServiceInterface {

    private final RentalRepository rentalRepo;
    private final VehicleRepository vehicleRepo;
    private final UserRepository userRepo;
    private final VehicleLocationService locationService;

    public RentalService(RentalRepository rentalRepo, VehicleRepository vehicleRepo, UserRepository userRepo, VehicleLocationService locationService) {
        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
        this.locationService = locationService;
    }

    public Rental rentVehicle(String userId, String vehicleId) {
        Vehicle vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Pojazd nie istnieje"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Użytkownik nie istnieje"));

        if (vehicle.isRented()) {
            throw new IllegalArgumentException("Pojazd jest aktualnie wypożyczony");
        }

        if (findActiveRentalByUserId(userId).isPresent())
            throw new IllegalArgumentException("Użytkownik ma aktualnie wypożyczony pojazd");

        vehicle.setRented(true);
        vehicleRepo.save(vehicle);

        Rental rental = Rental.builder()
                .user(user)
                .vehicle(vehicle)
                .rentDateTime(LocalDateTime.now())
                .build();

        rentalRepo.save(rental);
        return rental;
    }

    public Rental returnVehicle(String userId) {
        Optional<Rental> activeRental = rentalRepo.findByUserId(userId).stream()
                .filter(Rental::isActive)
                .findFirst();

        if (activeRental.isPresent()) {
            Rental rental = activeRental.get();

            locationService.validateReturnLocation(rental.getVehicle().getId());

            Vehicle vehicle = rental.getVehicle();
            vehicle.setRented(false);
            vehicleRepo.save(vehicle);

            rental.setReturnDateTime(LocalDateTime.now());
            rentalRepo.save(rental);
            return rental;
        }
        throw new IllegalArgumentException("Brak aktywnego wypożyczenia.");
    }

    @Transactional(readOnly = true)
    public List<Rental> findUserRentals(String userId) {
        return rentalRepo.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userHasActiveRental(String userId) {
        return findActiveRentalByUserId(userId).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean vehicleHasActiveRental(String vehicleId) {
        return vehicleRepo.findById(vehicleId)
                .map(Vehicle::isRented)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public Optional<Rental> findActiveRentalByUserId(String userId) {
        return rentalRepo.findByUserId(userId).stream()
                .filter(Rental::isActive)
                .findFirst();
    }

    @Transactional(readOnly = true)
    public List<Rental> findAllRentals() {
        return rentalRepo.findAll();
    }
}