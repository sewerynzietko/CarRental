package org.example.services.hibernate.impl;

import org.example.models.Rental;
import org.example.repositories.impl.hibernate.RentalHibernateRepository;
import org.example.repositories.impl.hibernate.UserHibernateRepository;
import org.example.repositories.impl.hibernate.VehicleHibernateRepository;
import org.example.services.hibernate.RentalServiceInterface;

import java.util.List;
import java.util.Optional;

public class RentalHibernateService implements RentalServiceInterface {
    private final RentalHibernateRepository rentalRepo;
    private final VehicleHibernateRepository vehicleRepo;
    private final UserHibernateRepository userRepo;

    public RentalHibernateService ( RentalHibernateRepository rentalRepo,
                                    VehicleHibernateRepository vehicleRepo,
                                    UserHibernateRepository userRepo ) {
        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
    }

    @Override
    public Rental rentVehicle ( String userId, String vehicleId ) {
        Transacion tx = null;

        try ()

    }

    @Override
    public Rental returnVehicle ( String userId ) {
        return null;
    }

    @Override
    public Optional<Rental> findActiveRentalByUserId ( String userId ) {
        return Optional.empty();
    }

    @Override
    public List<Rental> findAllRentals () {
        return null;
    }

    @Override
    public List<Rental> findUserRentals ( String userId ) {
        return null;
    }

    @Override
    public boolean userHasActiveRental ( String userId ) {
        return false;
    }

    @Override
    public boolean vehicleHasActiveRental ( String vehicleId ) {
        return false;
    }
}
