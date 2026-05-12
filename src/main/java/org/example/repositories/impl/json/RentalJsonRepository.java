package org.example.repositories.impl.json;

import com.google.gson.reflect.TypeToken;
import org.example.db.JsonFileStorage;
import org.example.models.Rental;
import org.example.models.User;
import org.example.repositories.RentalRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
@Repository
@Profile("json")
public class RentalJsonRepository implements RentalRepository {

    public JsonFileStorage<Rental> storage;

    private ArrayList<Rental> rentals;

    public RentalJsonRepository(@Value("${carrent.json.rentals-file}") String filename) {
        this.storage = new JsonFileStorage<>(filename, new TypeToken<List<Rental>>() {
        }.getType());
        this.rentals = new ArrayList<>(storage.load());
    }

    @Override
    public List<Rental> findAll() {
        return rentals.stream().map(Rental::copy).collect(Collectors.toList());
    }

    @Override
    public List<Rental> findById(String userId) {
        return rentals.stream().filter(rental -> rental.getUserId().equals(userId)).toList();
    }

    @Override
    public Rental save(Rental rental) {
        if (rental == null) throw new IllegalArgumentException("Rental cannot be null");

        Rental toSave = rental.copy();
        if (toSave.getId() == null || toSave.getId().isBlank()) {
            toSave.setId(UUID.randomUUID().toString());
        } else {
            rentals.removeIf(r -> r.getId().equals(toSave.getId()));
        }

        rentals.add(toSave);
        storage.save(rentals);
        return toSave.copy();
    }

    @Override
    public void deleteById(String id) {
        rentals.removeIf(rental -> rental.getId().equals(id));
        storage.save(rentals);
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        return rentals.stream()
                .filter(rental -> rental.getVehicleId().equals(vehicleId) && rental.isActive())
                .findFirst()
                .map(Rental::copy);
    }

    public Optional<Rental> findActiveByUserId(String userId) {
        return rentals.stream()
                .filter(rental -> rental.getUserId().equals(userId) && rental.isActive())
                .findFirst()
                .map(Rental::copy);
    }
}
