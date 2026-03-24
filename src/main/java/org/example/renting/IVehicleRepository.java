package org.example.renting;

import java.util.List;

public interface IVehicleRepository {
    void rentVehicle(String id); //ret boolean
    void returnVehicle(String id); //ret boolean
    List<Vehicle> getVehicles();
    void save(String filepath); // String filepath do usuniecia
    void load(String filepath); //tu tez
    void add(Vehicle vehicle);
    void remove(String id);
    Vehicle getVehicle(String id);
}
