package org.example.renting;

import java.util.List;

public interface IVehicleRepository {
    boolean rentVehicle(String id);
    boolean returnVehicle(String id);
    List<Vehicle> getVehicles();
    void save();
    void load();
    boolean add(Vehicle vehicle);
    boolean remove(String id);
    Vehicle getVehicle(String id);
}
