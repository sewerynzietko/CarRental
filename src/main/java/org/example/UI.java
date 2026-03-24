package org.example;

import org.example.authentication.*;
import org.example.renting.IVehicleRepository;
import org.example.renting.Vehicle;
import org.example.renting.VehicleRepositoryImpl;

import java.util.List;
import java.util.Scanner;

public class UI {
    private IVehicleRepository vehicleRepository;
    private IUserRepository userRepository;
    private Authentication authentication;
    private Scanner sc;

    public UI () {
        this.vehicleRepository = new VehicleRepositoryImpl();
        this.userRepository = new UserRepository();
        this.authentication = new Authentication(userRepository);
        this.sc = new Scanner(System.in);
    }

    void authenticateUser(){
        User user = null;
        while (true) {
            System.out.println("\n=== WYPOŻYCZALNIA POJAZDÓW ===");
            System.out.println("1 - Zaloguj się.");
            System.out.println("2 - Zarejestruj się");
            System.out.println("3 - Wyjscie");
            System.out.print("Wybierz opcję: ");
            int i = sc.nextInt();
            switch (i) {
                case 1 -> {
                    System.out.print("Podaj login: ");
                    sc.nextLine();
                    String login = sc.nextLine();
                    if (userRepository.getUser(login) == null) {
                        System.out.println("Użytkownik nie istnieje.");
                    } else {
                        for (int j = 0; j < 3; j++) {
                            System.out.print("Podaj hasło: ");
                            user = authentication.authenticate(login, sc.nextLine());
                            if (user == null) {
                                System.out.println("Hasło nie poprawne");
                            }
                        }
                        System.out.println("Hasło podane nie poprawnie 3 razy.");
                        System.out.println("Spróbuj ponownie.");
                    }
                }
                case 2 -> {
                    System.out.print("Podaj login: ");
                    sc.nextLine();
                    String login = sc.nextLine();
                    if (userRepository.getUser(login) != null) {
                        System.out.println("Użytkownik już istnieje.");
                    } else {
                        System.out.print("Podaj hasło: ");
                        String passwordHash = Authentication.hashPassword(sc.nextLine());
                        System.out.print("Podaj ponownie hasło: ");
                        if (passwordHash.equals(Authentication.hashPassword(sc.nextLine()))) {
                            User newUser = new User(login, passwordHash, Role.USER);
                            userRepository.addUser(newUser);
                            System.out.println("Zostałeś zarejestrowany.");
                        } else {
                            System.out.println("Hasła nie zgadzają się.");
                            System.out.println("Spróbuj zarejestrować się ponownie.");
                        }
                    }
                }
            }
        }
    }

    boolean functions(User user){
        System.out.println("\n=== WYPOŻYCZALNIA POJAZDÓW ===");
        System.out.println("1 - Wyświetl pojazdy ");
        System.out.println("2 - Wypożycz pojazd");
        System.out.println("3 - Zwróć pojazd");
        System.out.println("4 - Wyjście");
        System.out.print("Wybierz opcję: ");
        int i = sc.nextInt();
        sc.nextLine();
        switch (i){
            case 1:
                List<Vehicle> list = vehicleRepository.getVehicles();
                for (Vehicle v : list){
                    System.out.println(v.toString());
                }
                break;
            case 2:
                System.out.print("Podaj ID pojazdu do wypożyczenia: ");
                String rentId = sc.nextLine();
                vehicleRepository.rentVehicle(rentId);
                break;
            case 3:
                System.out.print("Podaj ID pojazdu zwracanego: ");
                String retId = sc.nextLine();
                vehicleRepository.returnVehicle(retId);
                break;
            case 4:
                System.out.println("Program ended.");
                return true;
        }
        return false;
    }
}
