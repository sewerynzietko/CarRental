package org.example.repositories;

import org.example.models.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository {
    List<Vehicle> findAll();
    Optional<Vehicle> findByVehicleId ( String vehicleId);
    Vehicle save(Vehicle vehicle);
    void deleteById(String id);
}
