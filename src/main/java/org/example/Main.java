package org.example;

import org.example.repositories.*;
import org.example.repositories.impl.jdbc.RentalJdbcRepository;
import org.example.repositories.impl.jdbc.UserJdbcRepository;
import org.example.repositories.impl.jdbc.VehicleCategoryConfigJdbcRepository;
import org.example.repositories.impl.jdbc.VehicleJdbcRepository;
import org.example.repositories.impl.json.RentalJsonRepository;
import org.example.repositories.impl.json.UserJsonRepository;
import org.example.repositories.impl.json.VehicleCategoryConfigJsonRepository;
import org.example.repositories.impl.json.VehicleJsonRepository;
import org.example.services.*;

public class Main {
    public static void main(String[] args) {

        VehicleRepository vehicleRepository;
        UserRepository userRepository;
        RentalRepository rentalRepository;
        VehicleCategoryConfigRepository categoryConfigRepository;
        if (args[0].equals("--storage=jdbc")) {
            vehicleRepository = new VehicleJdbcRepository();
            userRepository = new UserJdbcRepository();
            rentalRepository = new RentalJdbcRepository();
            categoryConfigRepository = new VehicleCategoryConfigJdbcRepository();
            //System.out.println(vehicleRepository.findAll());
        } else {
            vehicleRepository = new VehicleJsonRepository();
            userRepository = new UserJsonRepository();
            rentalRepository = new RentalJsonRepository();
            categoryConfigRepository = new VehicleCategoryConfigJsonRepository();
        }



        AuthService authService = new AuthService(userRepository);
        VehicleCategoryConfigService categoryConfigService = new VehicleCategoryConfigService(categoryConfigRepository);
        VehicleValidator vehicleValidator = new VehicleValidator(categoryConfigService);
        VehicleService vehicleService = new VehicleService(vehicleRepository, rentalRepository, vehicleValidator);
        RentalService rentalService = new RentalService(rentalRepository, vehicleRepository);
        UserService userService = new UserService(userRepository, rentalService);

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