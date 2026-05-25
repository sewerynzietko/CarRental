package org.example.repositories.impl.jpa;

import org.example.models.Vehicle;
import org.example.repositories.VehicleRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("jpa")
public interface VehicleJpaRepository extends JpaRepository<Vehicle, String>, VehicleRepository {
}