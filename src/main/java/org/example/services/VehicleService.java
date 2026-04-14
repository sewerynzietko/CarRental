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
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        vehicleValidator.validate(vehicle);
        vehicleRepository.save(vehicle);
        return vehicle;
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

    public Map<Object, Object> findAvailableVehicles() {
        return null;
    }

    public boolean isVehicleRented(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }

    public Optional<Vehicle> findById(String vehicleId) {
        return vehicleRepository.findById(vehicleId);
    }
}