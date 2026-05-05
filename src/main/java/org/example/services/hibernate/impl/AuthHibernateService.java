package org.example.services.hibernate.impl;

import org.example.HibernateConfig;
import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.impl.hibernate.UserHibernateRepository;
import org.example.services.hibernate.AuthServiceInterface;
import org.hibernate.Session;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthHibernateService implements AuthServiceInterface {
    private final UserHibernateRepository userRepo;

    public AuthHibernateService ( UserHibernateRepository userRepo ) {
        this.userRepo = userRepo;
    }

    @Override
    public boolean register ( String login, String rawPassword ) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            if (userRepo.findByLogin(login).isPresent()) {
                return false;
            }

            String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
            User newUser = User.builder()
                    .login(login)
                    .passwordHash(hashedPassword)
                    .role(Role.USER)
                    .build();

            userRepo.save(newUser);
            return true;
        }
    }

    @Override
    public Optional<User> login ( String login, String rawPassword ) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);
            Optional<User> userOpt = userRepo.findByLogin(login);

            if (userOpt.isPresent() && BCrypt.checkpw(rawPassword, userOpt.get().getPasswordHash())) {
                return userOpt;
            }
            return Optional.empty();
        }
    }

    private void setSession(Session session) {
        userRepo.setSession(session);
    }
}
