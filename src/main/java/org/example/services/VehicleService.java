package org.example.services;

import org.example.models.Rental;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.VehicleRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VehicleService {

    private RentalRepository rentalRepository;
    private VehicleRepository vehicleRepository;
    private VehicleValidator vehicleValidator;

    public VehicleService(VehicleRepository vehicleRepository, RentalRepository rentalRepository, VehicleValidator vehicleValidator) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleValidator = vehicleValidator;
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        vehicleValidator.validate(vehicle);
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
    }

    public void removeVehicle(String vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu."));

        boolean rented = rentalRepository
                .findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
        if (rented) {
            throw new IllegalArgumentException("Nie można usunć pojzdu, bo jest aktualnie wypożyczony.");
        }
        vehicleRepository.deleteById(vehicle.getId());
    }

    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.findAll().stream().filter(v -> !rentalRepository.findByVehicleIdAndReturnDateIsNull(v.getId()).isPresent()).toList();
    }

    public boolean isVehicleRented(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }

    public Vehicle findById(String vehicleId) {
        return vehicleRepository.findById(vehicleId).orElseThrow();
    }
}