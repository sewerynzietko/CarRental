package org.example.repositories;

import org.example.models.User;

import java.util.List;

public interface UserRepository {
    User getUser( String login);
    List<User> getUsers();
    boolean update(User updatedUser);
    boolean addUser(User user);
    boolean removeUser(User user);
}