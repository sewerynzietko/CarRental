package org.example.services;

import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.VehicleRepository;
import org.example.services.hibernate.RentalServiceInterface;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RentalService implements RentalServiceInterface {
    private final RentalRepository rentalRepo;
    private final VehicleRepository vehicleRepo;

    public RentalService(RentalRepository rentalRepo, VehicleRepository vehicleRepo) {
        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
    }

    @Override
    public Rental rentVehicle(String userId, String vehicleId) {
        Optional<Vehicle> vehicle = vehicleRepo.findById(vehicleId);
        if (vehicle.isEmpty()) throw new IllegalArgumentException("Pojazd nie istnieje");

        boolean isAlreadyRented = rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
        if (isAlreadyRented) throw new IllegalArgumentException("Pojazd jest aktualnie wypożyczony");

        Rental rental = Rental.builder()
                .user(User.builder().id(userId).build())
                .vehicle(vehicle.get())
                .rentDateTime(LocalDateTime.now())
                .build();

        return rentalRepo.save(rental);
    }

    @Override
    public Rental returnVehicle(String userId) {
        Rental rental = rentalRepo.findAll().stream()
                .filter(r -> r.getUser() != null && r.getUser().getId().equals(userId) && r.isActive())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nie masz aktualnie wypożyczonego pojazdu."));

        rental.setReturnDateTime(LocalDateTime.now());
        return rentalRepo.save(rental);
    }

    @Override
    public List<Rental> findUserRentals(String userId) {
        return rentalRepo.findById(userId);
    }

    @Override
    public boolean userHasActiveRental(String userId) {
        return findActiveRentalByUserId(userId).isPresent();
    }

    @Override
    public boolean vehicleHasActiveRental(String vehicleId) {
        return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }

    @Override
    public Optional<Rental> findActiveRentalByUserId(String userId) {
        return rentalRepo.findAll().stream()
                .filter(r -> r.getUser() != null && r.getUser().getId().equals(userId) && r.isActive())
                .findFirst();
    }

    @Override
    public List<Rental> findAllRentals() {
        return rentalRepo.findAll();
    }
}