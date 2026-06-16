package org.example.repositories.impl.jpa;

import org.example.models.Role;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Profile("jpa")
public interface RoleJpaRepository
        extends JpaRepository<Role, String> {
    Optional<Role> findByName( String name);
}
