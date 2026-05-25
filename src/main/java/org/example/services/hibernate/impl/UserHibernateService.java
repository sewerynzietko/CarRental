package org.example.services.hibernate.impl;

import org.example.HibernateConfig;
import org.example.models.User;
import org.example.repositories.impl.hibernate.RentalHibernateRepository;
import org.example.repositories.impl.hibernate.UserHibernateRepository;
import org.example.services.hibernate.UserServiceInterface;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UserHibernateService implements UserServiceInterface {
    private final UserHibernateRepository userRepo;
    private final RentalHibernateService rentalService;

    public UserHibernateService ( RentalHibernateService rentalService,
                                  UserHibernateRepository userRepo ) {
        this.userRepo = userRepo;
        this.rentalService = rentalService;
    }

    @Override
    public List<User> findAllUsers () {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);

            return userRepo.findAll();
        }
    }

    @Override
    public User findById ( String id ) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            setSession(session);

            return userRepo.findById(id).orElseThrow();
        }
    }

    @Override
    public void deleteUser(String userId, String loggedUserId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                setSession(session);
                User user = userRepo.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika."));

                if (userId.equals(loggedUserId)) {
                    throw new IllegalArgumentException("Nie można usunąć aktualnie zalogowanego użytkownika.");
                }

                boolean rental = rentalService.findActiveRentalByUserId(userId).isEmpty();

                setSession(session);

                if (!rental) {
                    throw new IllegalArgumentException("Nie można usunąć użytkownika, bo ma aktualnie wypożyczony pojazd.");
                }

                userRepo.deleteById(user.getId());
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    private void setSession(Session session) {
        userRepo.setSession(session);
    }
}
