package org.example.services.hibernate.impl;

import org.example.models.User;
import org.example.services.hibernate.UserServiceInterface;

import java.util.List;

public class UserHibernateService implements UserServiceInterface {
    @Override
    public List<User> findAllUsers () {
        return null;
    }

    @Override
    public User findById ( String id ) {
        return null;
    }

    @Override
    public void deleteUser ( String id, String loggedUserId ) {

    }
}
