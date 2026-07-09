package org.example.repositories;

import org.example.models.VehicleLocation;
import java.util.Optional;
import java.util.List;

public interface VehicleLocationRepository {
    Optional<VehicleLocation> findByVehicleId(String vehicleId);
    VehicleLocation save(VehicleLocation location);
    List<VehicleLocation> findAll();
}