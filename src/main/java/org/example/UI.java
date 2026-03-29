package org.example;

import org.example.authentication.*;
import org.example.renting.*;

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

    public void start(){
        boolean appRunning = true;
        User currentUser = null;

        while(appRunning){
            if (currentUser == null){
                System.out.println("\n=== WYPOŻYCZALNIA POJAZDÓW ===");
                System.out.println("1 - Zaloguj się.");
                System.out.println("2 - Zarejestruj się.");
                System.out.println("3 - Wyjscie");
                System.out.print("Wybierz opcję: ");

                String choice = sc.nextLine();

                switch (choice) {
                    case "1" -> {
                        System.out.print("Podaj login: ");
                        String login = sc.nextLine();
                        if (userRepository.getUser(login) == null) {
                            System.out.println("Użytkownik nie istnieje.");
                        } else {
                            boolean loggedIn = false;
                            for (int j = 0; j < 3; j++){
                                System.out.print("Podaj hasło: ");
                                currentUser = authentication.authenticate(login, sc.nextLine());

                                if (currentUser != null){
                                    System.out.println("Zalogowano pomyślnie.");
                                    loggedIn = true;
                                    break;
                                }
                                else{
                                    System.out.println("Hasło niepoprawne.");
                                }
                            }
                            if (!loggedIn) {
                                System.out.println("Podano błędne hasło 3 razy.");
                                System.out.println("Spróbuj ponownie.");
                            }
                        }
                    }
                    case "2" -> {
                        System.out.print("Podaj login: ");
                        String login = sc.nextLine();
                        if (userRepository.getUser(login) != null) {
                            System.out.println("Użytkownik już istnieje.");
                        } else {
                            System.out.print("Podaj hasło: ");
                            String password = sc.nextLine();
                            System.out.print("Podaj ponownie hasło: ");
                            String repeatedPassword = sc.nextLine();
                            if (password.equals(repeatedPassword)){
                                String passwordHash = Authentication.hashPassword(password);
                                User newUser = new User(login, passwordHash, Role.USER);
                                userRepository.addUser(newUser);
                                System.out.println("Zostałeś zarejestrowany.");
                            } else {
                                System.out.println("Hasła nie zgadzają się.");
                                System.out.println("Spróbuj zarejestrować się ponownie.");
                            }
                        }
                    }
                    case "3" -> {
                        appRunning = false;
                    }
                }
            } else {
                if (currentUser.getRole() == Role.ADMIN){
                    currentUser = showAdminMenu(currentUser);
                } else {
                    currentUser = showUserMenu(currentUser);
                }
            }
        }
    }

    private User showUserMenu(User user){
        System.out.println("\n=== WYPOŻYCZALNIA POJAZDÓW ===");
        System.out.println("1 - Wypożycz pojazd ");
        System.out.println("2 - Zwróć pojazd");
        System.out.println("3 - Dane i wypożyczony pojazd");
        System.out.println("4 - Wyloguj");
        System.out.print("Wybierz opcję: ");
        String choice = sc.nextLine();
        switch (choice){
            case "1":
                if(user.getRentedVehicleId() != null){
                    System.out.println("Masz już wypożyczony pojazd.");
                    break;
                }
                System.out.print("Podaj ID pojazdu do wypożyczenia: ");
                String rentId = sc.nextLine();
                if(vehicleRepository.getVehicle(rentId) != null) {
                    if(vehicleRepository.getVehicle(rentId).isRented()){
                        System.out.println("Podany pojazd został już wypożyczony");
                    } else {
                        if(vehicleRepository.rentVehicle(rentId)) {
                            user.setRentedVehicleId(rentId);
                            userRepository.update(user);
                            System.out.println("Wypożyczyłeś pojazd ID: " + rentId);
                        } else {
                            System.out.println("Wystąpił problem podczas wypożyczania pojazdu.");
                        }
                    }
                } else {
                    System.out.println("Pojazd o takim ID nie istnieje");
                }
                break;
            case "2":
                if (user.getRentedVehicleId() != null) {
                    String vehicleId = user.getRentedVehicleId();
                    if(vehicleRepository.returnVehicle(vehicleId)) {
                        user.setRentedVehicleId(null);
                        userRepository.update(user);
                        System.out.println("Pojazd został zwrócony.");
                    } else {
                        System.out.println("Wystąpił problem podczas zwracania pojazdu.");
                    }
                } else {
                    System.out.println("Nie masz żadnego pojazdu do zwrócenia.");
                }
                break;
            case "3":
                System.out.println("Twoje dane: " + user.getLogin());
                if (user.getRentedVehicleId() != null){
                    String vehicleId = user.getRentedVehicleId();
                    System.out.println("Wypożyczony pojazd ID: " + vehicleId);
                    Vehicle vehicle = vehicleRepository.getVehicle(vehicleId);
                    System.out.println(vehicle.toString());
                } else {
                    System.out.println("Brak wypożyczonych pojazdów");
                }
                break;
            case "4":
                System.out.println("Wylogowano");
                return null;
        }
        return user;
    }

    private User showAdminMenu(User admin){
        System.out.println("\n=== WYPOŻYCZALNIA POJAZDÓW ===");
        System.out.println("     === PANEL ADMINA ===     ");
        System.out.println("1 - Wyświetl pojazdy");
        System.out.println("2 - Dodaj pojazd");
        System.out.println("3 - Usuń pojazd");
        System.out.println("4 - Wyświetl użytkowników");
        System.out.println("5 - Usuń użytkownika");
        System.out.println("6 - Wyloguj");
        System.out.print("Wybierz opcję: ");
        String choice = sc.nextLine();
        switch (choice) {
            case "1":
                List<Vehicle> list = vehicleRepository.getVehicles();
                if (list.isEmpty()) {
                    System.out.println("Brak pojazdów w systemie.");
                } else {
                    for (Vehicle v : list) {
                        System.out.println(v.toString());
                    }
                }
                break;
            case "2":
                adminAddVehicle();
                break;
            case "3":
                System.out.print("Podaj ID pojazdu do usunięcia: ");
                String removeId = sc.nextLine();
                if(vehicleRepository.remove(removeId)) {
                    System.out.println("Pojazd o ID " + removeId + " został usunięty.");
                } else {
                    System.out.println("Wystąpił problem podczas usuwania pojazdu.");
                }
                break;
            case "4":
                adminDisplayUsers();
                break;
            case "5":
                System.out.print("Podaj login użytkownika do usunięcia: ");
                String removeLogin = sc.nextLine();
                User removeUser = userRepository.getUser(removeLogin);
                if (removeUser == null) {
                    System.out.println("Użytkownik o podanym loginie nie istnieje.");
                } else if(removeUser.getRentedVehicleId() != null){
                    System.out.println("Nie można usunąć użytkownika " + removeLogin + ".");
                    System.out.println("Użytkownik ma aktualnie wypożyczony pojazd.");
                } else {
                    if (userRepository.removeUser(removeUser)) {
                        System.out.println("Użytkownik " + removeLogin + " został usunięty.");
                    } else {
                        System.out.println("Wystąpił problem podczas usuwania użytkownika.");
                    }
                }
                break;
            case "6":
                return null;
        }
        return admin;
    }

    private void adminAddVehicle() {
        System.out.println("\nJaki typ pojazdu chcesz dodać?");
        System.out.println("1 - Samochód");
        System.out.println("2 - Motocykl (Motorcycle)");
        System.out.print("Wybierz opcję: ");
        String type = sc.nextLine();

        if (!type.equals("1") && !type.equals("2")) {
            System.out.println("Niepoprawny wybór typu pojazdu.");
            return;
        }

        try {
            System.out.print("Podaj ID: ");
            String id = sc.nextLine();

            if (vehicleRepository.getVehicle(id) != null) {
                System.out.println("Pojazd o takim ID już istnieje w bazie!");
                return;
            }

            System.out.print("Podaj markę (brand): ");
            String brand = sc.nextLine();

            System.out.print("Podaj model: ");
            String model = sc.nextLine();

            System.out.print("Podaj rocznik: ");
            int year = Integer.parseInt(sc.nextLine());

            System.out.print("Podaj cenę: ");
            float price = Float.parseFloat(sc.nextLine());

            if (type.equals("1")) {
                Car car = new Car(id, brand, model, year, price, false);
                if(vehicleRepository.add(car)) {
                    System.out.println("Samochód został dodany!");
                } else {
                    System.out.println("Nie udało się dodać pojazdu!");
                }
            } else {
                System.out.print("Podaj kategorię motocykla (np. AM, A1, A2, B, A): ");
                String category = sc.nextLine();
                Motorcycle motorcycle = new Motorcycle(id, brand, model, year, price, false, category);
                if(vehicleRepository.add(motorcycle)) {
                    System.out.println("Motor został dodany!");
                } else {
                    System.out.println("Nie udało się dodać pojazdu!");
                }
                System.out.println("Motocykl został dodany!");
            }

        } catch (NumberFormatException e) {
            System.out.println("Błąd: Wprowadzono niepoprawny format liczby dla rocznika lub ceny!");
        }
    }

    private void adminDisplayUsers() {
        List<User> users = userRepository.getUsers();
        if (users.isEmpty()) {
            System.out.println("Brak użytkowników do wyświetlenia.");
            return;
        }

        System.out.println("\n--- LISTA UŻYTKOWNIKÓW I ICH POJAZDÓW ---");
        for (User u : users) {
            System.out.println("Użytkownik: " + u.getLogin() + " | Rola: " + u.getRole());

            if (u.getRole() != Role.ADMIN) {
                String rentedId = u.getRentedVehicleId();
                if (rentedId != null && !rentedId.isEmpty()) {
                    Vehicle v = vehicleRepository.getVehicle(rentedId);
                    if (v != null) {
                        System.out.println(" -> Wypożyczony pojazd: " + v);
                    } else {
                        System.out.println(" -> Wypożyczony pojazd: [ID: " + rentedId + "] (Pojazd usunięty z bazy!)");
                    }
                } else {
                    System.out.println(" -> Brak wypożyczonych pojazdów.");
                }
            }
        }
    }
}