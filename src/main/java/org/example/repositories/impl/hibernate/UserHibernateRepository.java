package org.example.repositories.impl.hibernate;

import org.example.models.User;
import org.example.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserHibernateRepository implements UserRepository {
    @Override
    public List<User> findAll () {
        return null;
    }

    @Override
    public Optional<User> findById ( String id ) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByLogin ( String login ) {
        return Optional.empty();
    }

    @Override
    public User save ( User user ) {
        return null;
    }

    @Override
    public void deleteById ( String id ) {

    }
}
