package org.example.repositories;


import org.example.models.Rental;

import java.util.List;
import java.util.Optional;
public interface RentalRepository {
    List<Rental> findAll();
    Optional<Rental> findById(String id);
    List<Rental> findByUserId(String userid);
    Rental save(Rental rental);
    void deleteById(String id);
    Optional<Rental> findByVehicleIdAndReturnDateTimeIsNull(String vehicleId);
}