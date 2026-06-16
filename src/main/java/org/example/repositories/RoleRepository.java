package org.example.repositories;

import org.example.models.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {
    List<Role> findAll();
    Optional<Role> findById( String id);
    Optional<Role> findByName(String name);
    Role save(Role role);
}
