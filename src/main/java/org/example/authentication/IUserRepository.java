package org.example.authentication;

import java.util.List;

public interface IUserRepository {
    User getUser(String login);
    List<User> getUsers();
    boolean update(User updatedUser);
    boolean addUser(User user);
    boolean removeUser(User user);
}