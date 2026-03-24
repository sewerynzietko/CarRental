package org.example;

import org.example.authentication.Authentication;
import org.example.authentication.IUserRepository;
import org.example.authentication.User;
import org.example.authentication.UserRepository;
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

    User authenticateUser(){
        User user = null;
        System.out.println("\n=== WYPOŻYCZALNIA POJAZDÓW ===");
        System.out.println("1 - Zaloguj się.");
        System.out.println("2 - Wyjscie");
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
                        } else return user;
                    }
                        System.out.println("Hasło podane nie poprawnie 3 razy.");
                        System.out.println("Spróbuj ponownie.");
                }
            }
            case 2 -> {
                return null;
            }
        }
        return null;
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
