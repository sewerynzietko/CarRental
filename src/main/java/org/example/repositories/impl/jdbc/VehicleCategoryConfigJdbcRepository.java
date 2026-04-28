package org.example.repositories.impl.jdbc;

import com.google.gson.reflect.TypeToken;
import org.example.db.JsonFileStorage;
import org.example.models.VehicleCategoryConfig;
import org.example.repositories.VehicleCategoryConfigRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehicleCategoryConfigJdbcRepository implements VehicleCategoryConfigRepository {

    private final JsonFileStorage<VehicleCategoryConfig> storage =
            new JsonFileStorage<>("categoriesjdbc.json",
                    new TypeToken<List<VehicleCategoryConfig>>() {}.getType());

    private final List<VehicleCategoryConfig> configs;

    public VehicleCategoryConfigJdbcRepository() {
        this.configs = new ArrayList<>(storage.load());
    }

    public VehicleCategoryConfigJdbcRepository ( List<VehicleCategoryConfig> configs ) {
        this.configs = configs;
    }

    @Override
    public List<VehicleCategoryConfig> findAll() {
        List<VehicleCategoryConfig> copy = new ArrayList<>();
        for (VehicleCategoryConfig config : configs) {
            copy.add(config.copy());
        }
        return copy;
    }

    @Override
    public Optional<VehicleCategoryConfig> findByCategory(String category) {
        return configs.stream()
                .filter(c -> c.getCategory() != null)
                .filter(c -> c.getCategory().equalsIgnoreCase(category))
                .findFirst()
                .map(VehicleCategoryConfig::copy);
    }
}
