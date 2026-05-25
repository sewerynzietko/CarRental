package org.example.repositories.impl.jpa;

import org.example.models.VehicleCategoryConfig;
import org.example.repositories.VehicleCategoryConfigRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("jpa")
public interface VehicleCategoryConfigJpaRepository extends JpaRepository<VehicleCategoryConfig, String>, VehicleCategoryConfigRepository {
    Optional<VehicleCategoryConfig> findByCategory(String category);
}