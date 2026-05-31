package org.example.repositories.impl.jdbc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.models.VehicleCategoryConfig;
import org.example.repositories.VehicleCategoryConfigRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Profile("jdbc")
public class VehicleCategoryConfigJdbcRepository implements VehicleCategoryConfigRepository {

    private final DataSource dataSource;
    private final Gson gson = new Gson();

    public VehicleCategoryConfigJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    @Override
    public List<VehicleCategoryConfig> findAll() {
        List<VehicleCategoryConfig> configs = new ArrayList<>();
        String sql = "SELECT category, attributes FROM vehicle_category_config";
        Connection connection = getConnection();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                configs.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading vehicle category configs", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return configs;
    }

    @Override
    public Optional<VehicleCategoryConfig> findByCategory(String category) {
        String sql = "SELECT category, attributes FROM vehicle_category_config WHERE category = ?";
        Connection connection = getConnection();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, category);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading vehicle category config", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }

        return Optional.empty();
    }

    private VehicleCategoryConfig mapRow(ResultSet rs) throws SQLException {
        String category = rs.getString("category");
        String attributesJson = rs.getString("attributes");

        Map<String, String> attributes = new HashMap<>();
        if (attributesJson != null && !attributesJson.isBlank()) {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            attributes = gson.fromJson(attributesJson, type);
        }

        return VehicleCategoryConfig.builder()
                .category(category)
                .attributes(attributes)
                .build();
    }
}