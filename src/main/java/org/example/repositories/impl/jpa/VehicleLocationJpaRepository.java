package org.example.repositories.impl.jpa;

import org.example.models.VehicleLocation;
import org.example.repositories.VehicleLocationRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("jpa")
public interface VehicleLocationJpaRepository extends JpaRepository<VehicleLocation, String>, VehicleLocationRepository {
}