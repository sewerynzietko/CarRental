package org.example;

import org.example.repositories.*;
import org.example.repositories.impl.hibernate.RentalHibernateRepository;
import org.example.repositories.impl.hibernate.UserHibernateRepository;
import org.example.repositories.impl.hibernate.VehicleHibernateRepository;
import org.example.repositories.impl.jdbc.RentalJdbcRepository;
import org.example.repositories.impl.jdbc.UserJdbcRepository;
import org.example.repositories.impl.jdbc.VehicleCategoryConfigJdbcRepository;
import org.example.repositories.impl.jdbc.VehicleJdbcRepository;
import org.example.repositories.impl.json.RentalJsonRepository;
import org.example.repositories.impl.json.UserJsonRepository;
import org.example.repositories.impl.json.VehicleCategoryConfigJsonRepository;
import org.example.repositories.impl.json.VehicleJsonRepository;
import org.example.services.*;
import org.example.services.hibernate.AuthServiceInterface;
import org.example.services.hibernate.RentalServiceInterface;
import org.example.services.hibernate.UserServiceInterface;
import org.example.services.hibernate.VehicleServiceInterface;
import org.example.services.hibernate.impl.AuthHibernateService;
import org.example.services.hibernate.impl.RentalHibernateService;
import org.example.services.hibernate.impl.UserHibernateService;
import org.example.services.hibernate.impl.VehicleHibernateService;

public class Main {
    public static void main(String[] args) {
        VehicleServiceInterface vehicleService;
        UserServiceInterface userService;
        RentalServiceInterface rentalService;
        AuthServiceInterface authService;
        VehicleCategoryConfigService categoryConfigService;

        if (args.length > 0 && args[0].equals("--storage=hibernate")) {
            System.out.println("Uruchamianie w trybie HIBERNATE");

            VehicleHibernateRepository vRepo = new VehicleHibernateRepository();
            UserHibernateRepository uRepo = new UserHibernateRepository();
            RentalHibernateRepository rRepo = new RentalHibernateRepository();

            VehicleCategoryConfigRepository configRepo = new VehicleCategoryConfigJsonRepository();
            categoryConfigService = new VehicleCategoryConfigService(configRepo);
            VehicleValidator validator = new VehicleValidator(categoryConfigService);

            rentalService = new RentalHibernateService(rRepo, vRepo, uRepo);
            userService = new UserHibernateService((RentalHibernateService) rentalService, uRepo);
            vehicleService = new VehicleHibernateService(validator, rRepo, vRepo);
            authService = new AuthHibernateService(uRepo);

        } else {
            VehicleRepository vehicleRepository;
            UserRepository userRepository;
            RentalRepository rentalRepository;
            VehicleCategoryConfigRepository categoryConfigRepository;

            if (args.length > 0 && args[0].equals("--storage=jdbc")) {
                System.out.println("Uruchamianie w trybie JDBC");
                vehicleRepository = new VehicleJdbcRepository();
                userRepository = new UserJdbcRepository();
                rentalRepository = new RentalJdbcRepository();
                categoryConfigRepository = new VehicleCategoryConfigJdbcRepository();
            }
            else {
                System.out.println("Uruchamianie w trybie JSON");
                vehicleRepository = new VehicleJsonRepository();
                userRepository = new UserJsonRepository();
                rentalRepository = new RentalJsonRepository();
                categoryConfigRepository = new VehicleCategoryConfigJsonRepository();
            }

            categoryConfigService = new VehicleCategoryConfigService(categoryConfigRepository);
            VehicleValidator vehicleValidator = new VehicleValidator(categoryConfigService);

            authService = new AuthService(userRepository);
            rentalService = new RentalService(rentalRepository, vehicleRepository);
            vehicleService = new VehicleService(vehicleRepository, rentalRepository, vehicleValidator);
            userService = new UserService(userRepository, rentalService);
        }

        UI ui = new UI(
                authService,
                vehicleService,
                rentalService,
                userService,
                categoryConfigService
        );

        ui.start();
    }
}