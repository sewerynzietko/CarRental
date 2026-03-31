package org.example.repositories;

import org.example.models.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository {
    //boolean rentVehicle(String id); // do przeniesienia do rental
    //boolean returnVehicle(String id); // do przeniesienia do rental
    List<Vehicle> findAll();
    Optional<Vehicle> findById(String id);
    Vehicle save(Vehicle vehicle);
    void deleteById(String id);
}
