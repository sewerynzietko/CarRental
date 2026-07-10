package org.example.services.impl;

import org.example.models.Rental;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.VehicleRepository;
import org.example.services.VehicleServiceInterface;
import org.example.services.VehicleValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
@Service
@Transactional
public class VehicleService implements VehicleServiceInterface {

    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleValidator vehicleValidator;

    public VehicleService(VehicleRepository vehicleRepository, RentalRepository rentalRepository, VehicleValidator vehicleValidator) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleValidator = vehicleValidator;
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        vehicleValidator.validate(vehicle);
        return vehicleRepository.save(vehicle);
    }
    @Transactional(readOnly = true)
    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
    }

    public void removeVehicle(String vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu."));

        if (vehicle.isRented()) {
            throw new IllegalArgumentException("Nie można usunąć pojazdu, bo jest aktualnie wypożyczony.");
        }
        vehicleRepository.deleteById(vehicle.getId());
    }

    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.findAll().stream()
                .filter(v -> !v.isRented())
                .toList();
    }

    public boolean isVehicleRented(String vehicleId) {
        return findById(vehicleId).isRented();
    }

    public Vehicle findById(String vehicleId) {
        return vehicleRepository.findById(vehicleId).orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu."));
    }
}