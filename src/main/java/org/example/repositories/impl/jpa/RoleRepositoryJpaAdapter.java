package org.example.repositories.impl.jpa;

import org.example.models.Role;
import org.example.repositories.RoleRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jpa")
public class RoleRepositoryJpaAdapter implements RoleRepository {
    private final RoleJpaRepository delegate;
    public RoleRepositoryJpaAdapter(RoleJpaRepository delegate) {
        this.delegate = delegate;
    }
    public List<Role> findAll() { return delegate.findAll(); }
    public Optional<Role> findById( String id) { return delegate.findById(id); }
    public Optional<Role> findByName(String name) { return delegate.findByName(name); }
    public Role save(Role role) {
        if (role.getId() == null || role.getId().isBlank()) {
            role.setId(UUID.randomUUID().toString());
        }
        return delegate.save(role);
    }
}
