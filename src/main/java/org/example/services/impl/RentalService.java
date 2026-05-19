package org.example.services.impl;

import org.example.models.Rental;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.VehicleRepository;
import org.example.services.RentalServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service
@Transactional
public class RentalService implements RentalServiceInterface{
    private final RentalRepository rentalRepo;
    private final VehicleRepository vehicleRepo;

    public RentalService(RentalRepository rentalRepo, VehicleRepository vehicleRepo) {
        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
    }

    public Rental rentVehicle( String userId, String vehicleId) {
        Optional<Vehicle> vehicle = vehicleRepo.findById(vehicleId);
        if (vehicle.isEmpty()) throw new IllegalArgumentException("Pojazd nie istnieje");

        boolean isAlreadyRented = rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
        if (isAlreadyRented) throw new IllegalArgumentException("Pojazd jest aktualnie wypożyczony");

        Rental rental = Rental.builder()
                .userId(userId)
                .vehicleId(vehicleId)
                .rentDateTime(LocalDateTime.now())
                .build();

        rentalRepo.save(rental);
        return rental;
    }

    public Rental returnVehicle( String userId) {
        Optional<Rental> activeRental = rentalRepo.findAll().stream()
                .filter(r -> r.getUserId().equals(userId) && r.isActive())
                .findFirst();

        if (activeRental.isPresent()) {
            Rental rental = activeRental.get();
            rental.setReturnDateTime(LocalDateTime.now());
            rentalRepo.save(rental);
            return rental;
        }
        throw new IllegalArgumentException("Brak aktywnego wypożyczenia.");
    }


    @Transactional(readOnly = true)
    public List<Rental> findUserRentals(String userId) {
        return rentalRepo.findById(userId);
    }

    @Override
    public boolean userHasActiveRental ( String userId ) {
        return false;
    }

    @Override
    public boolean vehicleHasActiveRental ( String vehicleId ) {
        return false;
    }

    public Optional<Rental> findActiveRentalByUserId(String userId) {
        return rentalRepo.findAll().stream()
                .filter(r -> r.getUserId().equals(userId) && r.isActive())
                .findFirst();
    }

    public List<Rental> findAllRentals() {
        return rentalRepo.findAll();
    }
}