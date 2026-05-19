package org.example.repositories.impl.jdbc;

import org.example.db.JdbcConnectionManager;
import org.example.models.Rental;
import org.example.repositories.RentalRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jdbc")
public class RentalJdbcRepository implements RentalRepository {
    @Override
    public List<Rental> findAll () {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT id, vehicle_id, user_id, rent_date, return_date FROM rental";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                rentals.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rentals", e);
        }

        return rentals;
    }

    @Override
    public List<Rental> findById ( String id ) {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rental WHERE user_id = ?";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rentals.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rentals", e);
        }
        return rentals;
    }

    @Override
    public Rental save ( Rental rental ) {
        if (rental.getId() == null || rental.getId().isBlank()) {
            rental.setId(UUID.randomUUID().toString());
        }

        String sql = "INSERT INTO rental (id, vehicle_id, user_id, rent_date, return_date) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET " +
                "vehicle_id = EXCLUDED.vehicle_id, user_id = EXCLUDED.user_id, " +
                "rent_date = EXCLUDED.rent_date, return_date = EXCLUDED.return_date";

        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, rental.getId());
            stmt.setString(2, rental.getVehicleId());
            stmt.setString(3, rental.getUserId());
            stmt.setString(4, rental.getRentDateTime() != null ? rental.getRentDateTime().toString() : null);
            stmt.setString(5, rental.getReturnDateTime() != null ? rental.getReturnDateTime().toString() : null);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while saving rentals", e);
        }
        return rental;
    }

    @Override
    public void deleteById ( String id ) {
        String sql = "DELETE FROM rental WHERE id = ?";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting rentals", e);
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull ( String vehicleId ) {
        String sql = "SELECT * FROM rental WHERE vehicle_id = ? AND return_date IS NULL";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String rentDateStr = rs.getString("rent_date");
                    Rental rental = Rental.builder()
                            .id(rs.getString("id"))
                            .vehicleId(rs.getString("vehicle_id"))
                            .userId(rs.getString("user_id"))
                            .rentDateTime(rentDateStr != null ? LocalDateTime.parse(rentDateStr) : null)
                            .returnDateTime(null)
                            .build();
                    return Optional.of(rental);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rentals", e);
        }
        return Optional.empty();
    }

    private Rental mapRow(ResultSet rs) throws SQLException {
        String rentDateStr = rs.getString("rent_date");
        String returnDateStr = rs.getString("return_date");
        return Rental.builder()
                .id(rs.getString("id"))
                .vehicleId(rs.getString("vehicle_id"))
                .userId(rs.getString("user_id"))
                .rentDateTime(rentDateStr != null ? LocalDateTime.parse(rentDateStr) : null)
                .returnDateTime(returnDateStr != null ? LocalDateTime.parse(returnDateStr) : null)
                .build();
    }
}
