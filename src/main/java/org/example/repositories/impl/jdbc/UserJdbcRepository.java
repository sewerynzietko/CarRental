package org.example.repositories.impl.jdbc;

import org.example.db.JdbcConnectionManager;
import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserJdbcRepository implements UserRepository {
    @Override
    public List<User> findAll () {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, login, password_hash, role FROM users";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading users", e);
        }

        return users;
    }

    @Override
    public Optional<User> findById ( String id ) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = User.builder()
                            .id(rs.getString("id"))
                            .login(rs.getString("login"))
                            .passwordHash(rs.getString("password_hash"))
                            .role(Role.valueOf(rs.getString("role")))
                            .build();
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading users", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByLogin ( String login ) {
        String sql = "SELECT * FROM users WHERE login = ?";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = User.builder()
                            .id(rs.getString("id"))
                            .login(rs.getString("login"))
                            .passwordHash(rs.getString("password_hash"))
                            .role(Role.valueOf(rs.getString("role")))
                            .build();
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading users", e);
        }
        return Optional.empty();
    }

    @Override
    public User save ( User user ) {
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(UUID.randomUUID().toString());
        } else {
            deleteById(user.getId());
        }
        String sql = "INSERT INTO users (id, login, password_hash, role) VALUES (?, ?, ?, ?::jsonb)";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, String.valueOf(user.getRole()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while saving users", e);
        }
        return user;
    }

    @Override
    public void deleteById ( String id ) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting users", e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getString("id"))
                .login(rs.getString("login"))
                .passwordHash(rs.getString("password_hash"))
                .role(Role.valueOf(rs.getString("role")))
                .build();
    }
}
