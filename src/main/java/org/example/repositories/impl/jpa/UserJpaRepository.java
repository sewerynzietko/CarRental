package org.example.repositories.impl.jpa;

import org.example.models.User;
import org.example.repositories.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("jpa")
public interface UserJpaRepository extends JpaRepository<User, String>, UserRepository {
    Optional<User> findByLogin(String login);
}