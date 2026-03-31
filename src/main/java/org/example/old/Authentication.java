package org.example.old;

import org.apache.commons.codec.digest.DigestUtils;
import org.example.repositories.UserRepository;
import org.example.models.User;

public class Authentication {

    UserRepository userRepository;
    public Authentication ( UserRepository userRepository ) {
        this.userRepository = userRepository;
    }

    public static String hashPassword ( String password ) {
        return DigestUtils.sha256Hex(password);
    }

    public User authenticate ( String login, String password ) {
        User user = userRepository.getUser(login);
        if (user == null) return null;
        else if(user.getPasswordHash().equals(hashPassword(password))) return user;
        else return null;
    }
}
