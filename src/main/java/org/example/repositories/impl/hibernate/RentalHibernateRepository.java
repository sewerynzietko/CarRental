package org.example.repositories.impl.hibernate;

import org.example.models.Rental;
import org.example.repositories.RentalRepository;

import java.util.List;
import java.util.Optional;

public class RentalHibernateRepository implements RentalRepository {
    @Override
    public List<Rental> findAll () {
        return null;
    }

    @Override
    public List<Rental> findById ( String id ) {
        return null;
    }

    @Override
    public Rental save ( Rental rental ) {
        return null;
    }

    @Override
    public void deleteById ( String id ) {

    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull ( String vehicleId ) {
        return Optional.empty();
    }
}
