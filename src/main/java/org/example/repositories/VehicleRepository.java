package org.example.repositories;

import org.example.models.Vehicle;

import java.util.List;

public interface VehicleRepository {
    boolean rentVehicle(String id);
    boolean returnVehicle(String id);
    List<Vehicle> getVehicles();
    void save();
    void load();
    boolean add(Vehicle vehicle);
    boolean remove(String id);
    Vehicle getVehicle(String id);
}
