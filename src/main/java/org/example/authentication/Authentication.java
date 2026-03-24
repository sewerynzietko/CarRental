package org.example.authentication;

import org.apache.commons.codec.digest.DigestUtils;
public class Authentication {

    IUserRepository userRepository;
    public Authentication ( IUserRepository userRepository ) {
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
