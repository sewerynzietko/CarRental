package org.example.services.hibernate.impl;

import org.example.models.Vehicle;
import org.example.services.hibernate.VehicleServiceInterface;

import java.util.List;

public class VehicleHibernateService implements VehicleServiceInterface {
    @Override
    public List<Vehicle> findAllVehicles () {
        return null;
    }

    @Override
    public List<Vehicle> findAvailableVehicles () {
        return null;
    }

    @Override
    public Vehicle findById ( String id ) {
        return null;
    }

    @Override
    public Vehicle addVehicle ( Vehicle vehicle ) {
        return null;
    }

    @Override
    public void removeVehicle ( String vehicleId ) {

    }

    @Override
    public boolean isVehicleRented ( String vehicleId ) {
        return false;
    }
}
