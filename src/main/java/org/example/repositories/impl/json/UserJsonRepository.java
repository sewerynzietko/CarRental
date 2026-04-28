package org.example.repositories.impl.json;

import com.google.gson.reflect.TypeToken;
import org.example.db.JsonFileStorage;
import org.example.models.Role;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.UserRepository;

import java.io.*;
import java.util.*;

public class UserJsonRepository implements UserRepository {

    private final JsonFileStorage<User> storage =
            new JsonFileStorage<>(
                    "users.json",
                    new TypeToken<List<User>>()
                    {}.getType()
            );

    private ArrayList<User> users;

    public UserJsonRepository () {
        users = new ArrayList<>(storage.load());
    }

    public List<User> findAll () {
        List<User> copy = new ArrayList<>();
        for(User u : users) {
            copy.add(u.copy());
        }
        return copy;
    }

    @Override
    public Optional<User> findById ( String id ) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .map(User::copy);
    }

    @Override
    public Optional<User> findByLogin ( String login ) {
        return users.stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst()
                .map(User::copy);
    }

    @Override
    public User save ( User user ) {
        if (user == null){
            throw new IllegalArgumentException("user cannot be null");
        }
        User toSave = user.copy();
        if (toSave.getId() == null || toSave.getId().isBlank()){
            toSave.setId(UUID.randomUUID().toString());
        } else {
            users.removeIf(u -> u.getId().equals(toSave.getId()));
        }
        users.add(toSave);
        storage.save(users);
        return toSave.copy();
    }

    @Override
    public void deleteById ( String id ) {
        users.removeIf(user -> user.getId().equals(id));
        storage.save(users);
    }
}