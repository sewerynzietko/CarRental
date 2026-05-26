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
public class RentalService implements RentalServiceInterface {

    private final RentalRepository rentalRepo;
    private final VehicleRepository vehicleRepo;

    public RentalService(RentalRepository rentalRepo, VehicleRepository vehicleRepo) {
        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
    }

    public Rental rentVehicle(String userId, String vehicleId) {
        Optional<Vehicle> vehicle = vehicleRepo.findByVehicleId(vehicleId);
        if (vehicle.isEmpty()) throw new IllegalArgumentException("Pojazd nie istnieje");

        boolean isAlreadyRented = rentalRepo.findByVehicleIdAndReturnDateTimeIsNull(vehicleId).isPresent();
        if (isAlreadyRented) throw new IllegalArgumentException("Pojazd jest aktualnie wypożyczony");

        if (findActiveRentalByUserId(userId).isPresent())
            throw new IllegalArgumentException("Użytkownik ma aktualnie wypożyczony pojazd");

        Rental rental = Rental.builder()
                .userId(userId)
                .vehicleId(vehicleId)
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
        return rentalRepo.findByVehicleIdAndReturnDateTimeIsNull(vehicleId).isPresent();
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