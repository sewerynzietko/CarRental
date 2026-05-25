package org.example.repositories.impl.jdbc;

import org.example.models.Rental;
import org.example.repositories.RentalRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jdbc")
public class RentalJdbcRepository implements RentalRepository {

    private final DataSource dataSource;

    public RentalJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    @Override
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT id, vehicle_id, user_id, rent_date, return_date FROM rental";
        Connection connection = getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                rentals.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rentals", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return rentals;
    }

    @Override
    public List<Rental> findByUserId(String userid) {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT id, vehicle_id, user_id, rent_date, return_date FROM rental WHERE user_id = ?";
        Connection connection = getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userid);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rentals.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rentals by user", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return rentals;
    }

    @Override
    public Rental save(Rental rental) {
        if (rental.getId() == null || rental.getId().isBlank()) {
            rental.setId(UUID.randomUUID().toString());
        }

        String sql = "INSERT INTO rental (id, vehicle_id, user_id, rent_date, return_date) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET " +
                "vehicle_id = EXCLUDED.vehicle_id, user_id = EXCLUDED.user_id, " +
                "rent_date = EXCLUDED.rent_date, return_date = EXCLUDED.return_date";

        Connection connection = getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, rental.getId());
            stmt.setString(2, rental.getVehicleId());
            stmt.setString(3, rental.getUserId());
            stmt.setObject(4, rental.getRentDateTime());
            stmt.setObject(5, rental.getReturnDateTime());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while saving rental", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return rental;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM rental WHERE id = ?";
        Connection connection = getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting rental", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateTimeIsNull(String vehicleId) {
        String sql = "SELECT id, vehicle_id, user_id, rent_date, return_date FROM rental WHERE vehicle_id = ? AND return_date IS NULL";
        Connection connection = getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading active rental", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return Optional.empty();
    }

    private Rental mapRow(ResultSet rs) throws SQLException {
        return Rental.builder()
                .id(rs.getString("id"))
                .vehicleId(rs.getString("vehicle_id"))
                .userId(rs.getString("user_id"))
                .rentDateTime(rs.getObject("rent_date", LocalDateTime.class))
                .returnDateTime(rs.getObject("return_date", LocalDateTime.class))
                .build();
    }
}