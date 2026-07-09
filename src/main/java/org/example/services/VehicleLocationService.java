package org.example.services;

import org.example.dto.LocationRequest;
import org.example.models.VehicleLocation;

public interface VehicleLocationService {
    void updateLocation(String vehicleId, LocationRequest request);
    void validateReturnLocation(String vehicleId);
    void simulateGpsMovement();
    VehicleLocation getLocation(String vehicleId);
}