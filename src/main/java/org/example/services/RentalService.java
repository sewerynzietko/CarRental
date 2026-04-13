package org.example.services;

import org.example.models.Rental;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.VehicleRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RentalService {
    private final RentalRepository rentalRepo;
    private final VehicleRepository vehicleRepo;

    public RentalService(RentalRepository rentalRepo, VehicleRepository vehicleRepo) {
        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
    }

    public boolean rentVehicle(String userId, String vehicleId) {
        Optional<Vehicle> vehicle = vehicleRepo.findById(vehicleId);
        if (vehicle.isEmpty()) return false;

        boolean isAlreadyRented = rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
        if (isAlreadyRented) return false;

        Rental rental = Rental.builder()
                .userId(userId)
                .vehicleId(vehicleId)
                .rentDateTime(LocalDateTime.now())
                .build();

        rentalRepo.save(rental);
        return true;
    }

    public boolean returnVehicle(String userId) {
        Optional<Rental> activeRental = rentalRepo.findAll().stream()
                .filter(r -> r.getUserId().equals(userId) && r.isActive())
                .findFirst();

        if (activeRental.isPresent()) {
            Rental rental = activeRental.get();
            rental.setReturnDateTime(LocalDateTime.now());
            rentalRepo.save(rental);
            return true;
        }
        return false;
    }

    public Optional<Rental> getActiveRentalForUser(String userId) {
        return rentalRepo.findAll().stream()
                .filter(r -> r.getUserId().equals(userId) && r.isActive())
                .findFirst();
    }

    public List<Vehicle> getAvailableVehicles() {
        List<String> rentedVehicleIds = rentalRepo.findAll().stream()
                .filter(Rental::isActive)
                .map(Rental::getVehicleId)
                .toList();

        return vehicleRepo.findAll().stream()
                .filter(v -> !rentedVehicleIds.contains(v.getId()))
                .collect(Collectors.toList());
    }

    public String getRentalStatusForVehicle(String vehicleId) {
        return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent() ? "Wypożyczony" : "Dostępny";
    }

    public List<Rental> getAllRentalsForVehicle(String vehicleId) {
        return rentalRepo.findAll().stream()
                .filter(r -> r.getVehicleId().equals(vehicleId))
                .sorted(Comparator.comparing(Rental::getRentDateTime).reversed())
                .collect(Collectors.toList());
    }
}