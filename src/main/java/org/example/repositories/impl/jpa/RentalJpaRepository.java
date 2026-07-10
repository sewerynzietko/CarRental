package org.example.repositories.impl.jpa;

import org.example.models.Rental;
import org.example.repositories.RentalRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("jpa")
public interface RentalJpaRepository extends JpaRepository<Rental, String>, RentalRepository {

    List<Rental> findByUserId(String userId);
    Rental save(Rental rental);
}