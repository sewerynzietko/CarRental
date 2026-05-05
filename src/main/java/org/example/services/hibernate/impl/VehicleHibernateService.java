package org.example.services.hibernate.impl;

import org.example.HibernateConfig;
import org.example.models.Vehicle;
import org.example.repositories.impl.hibernate.RentalHibernateRepository;
import org.example.repositories.impl.hibernate.VehicleHibernateRepository;
import org.example.services.VehicleValidator;
import org.example.services.hibernate.VehicleServiceInterface;
import org.hibernate.Session;

import java.util.List;

public class VehicleHibernateService implements VehicleServiceInterface {

    private VehicleValidator vehicleValidator;
    private final RentalHibernateRepository rentalRepo;
    private final VehicleHibernateRepository vehicleRepo;

    public VehicleHibernateService ( VehicleValidator vehicleValidator, RentalHibernateRepository rentalRepo, VehicleHibernateRepository vehicleRepo ) {
        this.vehicleValidator = vehicleValidator;
        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
    }

    @Override
    public List<Vehicle> findAllVehicles () {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return vehicleRepo.findAll();
        }
    }

    @Override
    public List<Vehicle> findAvailableVehicles () {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return vehicleRepo.findAll().stream().filter(v -> !rentalRepo.findByVehicleIdAndReturnDateIsNull(v.getId()).isPresent()).toList();
        }
    }

    @Override
    public Vehicle findById ( String vehicleId ) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return vehicleRepo.findById(vehicleId).orElseThrow();
        }
    }

    @Override
    public Vehicle addVehicle ( Vehicle vehicle ) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            vehicleValidator.validate(vehicle);
            return vehicleRepo.save(vehicle);
        }
    }

    @Override
    public void removeVehicle ( String vehicleId ) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            Vehicle vehicle = vehicleRepo.findById(vehicleId)
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu."));

            boolean rented = rentalRepo
                    .findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
            if (rented) {
                throw new IllegalArgumentException("Nie można usunć pojzdu, bo jest aktualnie wypożyczony.");
            }
            vehicleRepo.deleteById(vehicle.getId());
        }
    }

    @Override
    public boolean isVehicleRented ( String vehicleId ) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
        }
    }

    private void setSession(Session session) {
        rentalRepo.setSession(session);
        vehicleRepo.setSession(session);
    }
}
