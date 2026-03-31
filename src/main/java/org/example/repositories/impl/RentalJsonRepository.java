package org.example.repositories.impl;

import com.google.gson.reflect.TypeToken;
import org.example.db.JsonFileStorage;
import org.example.models.Rental;
import org.example.repositories.RentalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RentalJsonRepository implements RentalRepository {

    private final JsonFileStorage<Rental> storage =
            new JsonFileStorage<>(
                    "rentals.json",
                    new TypeToken<List<Rental>>()
                    {}.getType()
            );

    private ArrayList<Rental> rentals;

    @Override
    public List<Rental> findAll () {
        return null;
    }

    @Override
    public Optional<Rental> findById ( String id ) {
        return Optional.empty();
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
