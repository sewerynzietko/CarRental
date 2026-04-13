package org.example;

import org.example.models.Role;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.models.Rental;
import org.example.repositories.RentalRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleRepository;
import org.example.repositories.impl.RentalJsonRepository;
import org.example.repositories.impl.UserJsonRepository;
import org.example.repositories.impl.VehicleJsonRepository;
import org.example.services.AuthService;
import org.example.services.RentalService;

import java.util.*;

public class UI {
    private final Scanner sc;
    private final AuthService authService;
    private final RentalService rentalService;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public UI() {
        this.sc = new Scanner(System.in);
        this.userRepository = new UserJsonRepository();
        this.vehicleRepository = new VehicleJsonRepository();
        RentalRepository rentalRepository = new RentalJsonRepository();

        this.authService = new AuthService(userRepository);
        this.rentalService = new RentalService(rentalRepository, vehicleRepository);
    }

    public void start() {
        boolean appRunning = true;
        User currentUser = null;

        while (appRunning) {
            if (currentUser == null) {
                currentUser = showGuestMenu();
                if (currentUser != null && currentUser.getLogin().equals("EXIT")) {
                    appRunning = false;
                }
            } else {
                if (currentUser.getRole() == Role.ADMIN) {
                    currentUser = showAdminMenu(currentUser);
                } else {
                    currentUser = showUserMenu(currentUser);
                }
            }
        }
        System.out.println("Do widzenia!");
    }

    private User showGuestMenu() {
        System.out.println("\n=== WYPOŻYCZALNIA POJAZDÓW ===");
        System.out.println("1 - Zaloguj się");
        System.out.println("2 - Zarejestruj się");
        System.out.println("3 - Wyjście");
        System.out.print("Wybierz opcję: ");

        String choice = sc.nextLine();

        switch (choice) {
            case "1":
                return handleLogin();
            case "2":
                handleRegister();
                return null;
            case "3":
                return User.builder().login("EXIT").build();
            default:
                System.out.println("Niepoprawna opcja.");
                return null;
        }
    }

    private User handleLogin() {
        for (int i = 0; i < 3; i++) {
            System.out.print("Podaj login: ");
            String login = sc.nextLine();
            System.out.print("Podaj hasło: ");
            String password = sc.nextLine();

            Optional<User> loggedUser = authService.login(login, password);
            if (loggedUser.isPresent()) {
                System.out.println("Zalogowano pomyślnie.");
                return loggedUser.get();
            } else {
                System.out.println("Błędny login lub hasło. Próba " + (i + 1) + "/3");
            }
        }
        System.out.println("Wykorzystano limit prób.");
        return null;
    }

    private void handleRegister() {
        System.out.print("Podaj nowy login: ");
        String login = sc.nextLine();
        System.out.print("Podaj hasło: ");
        String password = sc.nextLine();
        System.out.print("Powtórz hasło: ");
        String repeat = sc.nextLine();

        if (!password.equals(repeat)) {
            System.out.println("Hasła nie są identyczne.");
            return;
        }

        if (authService.register(login, password)) {
            System.out.println("Zarejestrowano pomyślnie! Możesz się teraz zalogować.");
        } else {
            System.out.println("Użytkownik o podanym loginie już istnieje.");
        }
    }

    private User showUserMenu(User user) {
        System.out.println("\n=== PANEL UŻYTKOWNIKA (" + user.getLogin() + ") ===");
        System.out.println("1 - Zobacz dostępne pojazdy");
        System.out.println("2 - Wypożycz pojazd");
        System.out.println("3 - Zwróć pojazd");
        System.out.println("4 - Moje aktywne wypożyczenie");
        System.out.println("5 - Wyloguj");
        System.out.print("Wybierz opcję: ");

        String choice = sc.nextLine();
        switch (choice) {
            case "1":
                displayAvailableVehicles();
                break;
            case "2":
                handleRentVehicle(user);
                break;
            case "3":
                handleReturnVehicle(user);
                break;
            case "4":
                displayUserRentalInfo(user);
                break;
            case "5":
                System.out.println("Wylogowano.");
                return null;
            default:
                System.out.println("Niepoprawna opcja.");
        }
        return user;
    }

    private void displayAvailableVehicles() {
        List<Vehicle> available = rentalService.getAvailableVehicles();
        if (available.isEmpty()) {
            System.out.println("Brak dostępnych pojazdów.");
        } else {
            System.out.println("--- Dostępne pojazdy ---");
            available.forEach(v -> System.out.println(v.getId() + " | " + v.getBrand() + " " + v.getModel() + " (" + v.getPrice() + " PLN)"));
        }
    }

    private void handleRentVehicle(User user) {
        if (rentalService.getActiveRentalForUser(user.getId()).isPresent()) {
            System.out.println("Masz już aktywne wypożyczenie! Zwróć pojazd, zanim wypożyczysz nowy.");
            return;
        }

        displayAvailableVehicles();
        System.out.print("Podaj ID pojazdu do wypożyczenia: ");
        String vehicleId = sc.nextLine();

        if (rentalService.rentVehicle(user.getId(), vehicleId)) {
            System.out.println("Pomyślnie wypożyczono pojazd!");
        } else {
            System.out.println("Nie udało się wypożyczyć pojazdu (nie istnieje lub jest zajęty).");
        }
    }

    private void handleReturnVehicle(User user) {
        if (rentalService.returnVehicle(user.getId())) {
            System.out.println("Pomyślnie zwrócono pojazd.");
        } else {
            System.out.println("Nie masz aktywnego wypożyczenia.");
        }
    }

    private void displayUserRentalInfo(User user) {
        Optional<Rental> activeRental = rentalService.getActiveRentalForUser(user.getId());
        if (activeRental.isPresent()) {
            Rental rental = activeRental.get();
            Vehicle v = vehicleRepository.findById(rental.getVehicleId()).orElse(null);
            System.out.println("Wypożyczony pojazd: " + (v != null ? v.getBrand() + " " + v.getModel() : "Pojazd usunięty"));
            System.out.println("Data wypożyczenia: " + rental.getRentDateTime());
        } else {
            System.out.println("Brak aktywnego wypożyczenia.");
        }
    }

    private User showAdminMenu(User admin) {
        System.out.println("\n=== PANEL ADMINA ===");
        System.out.println("1 - Wyświetl wszystkie pojazdy (ze statusem)");
        System.out.println("2 - Dodaj pojazd");
        System.out.println("3 - Usuń pojazd");
        System.out.println("4 - Wyświetl użytkowników");
        System.out.println("5 - Usuń użytkownika");
        System.out.println("6 - Wyloguj");
        System.out.print("Wybierz opcję: ");

        String choice = sc.nextLine();
        switch (choice) {
            case "1":
                adminDisplayAllVehicles();
                break;
            case "2":
                adminAddVehicle();
                break;
            case "3":
                adminRemoveVehicle();
                break;
            case "4":
                adminDisplayUsers();
                break;
            case "5":
                adminRemoveUser();
                break;
            case "6":
                return null;
            default:
                System.out.println("Niepoprawna opcja.");
        }
        return admin;
    }

    private void adminDisplayAllVehicles() {
        List<Vehicle> list = vehicleRepository.findAll();
        if (list.isEmpty()) {
            System.out.println("Brak pojazdów w systemie.");
        } else {
            System.out.println("\n--- PANEL ZARZĄDZANIA POJAZDAMI (Historia i Statusy) ---");
            for (Vehicle v : list) {
                String status = rentalService.getRentalStatusForVehicle(v.getId());
                System.out.println("\nPOJAZD: " + v.getBrand() + " " + v.getModel() + " (ID: " + v.getId() + ")");
                System.out.println("Kategoria: " + v.getCategory() + " | Tablice: " + v.getPlate() + " | Status: " + status);
                
                List<Rental> history = rentalService.getAllRentalsForVehicle(v.getId());
                if (!history.isEmpty()) {
                    System.out.println("  HISTORIA WYPOŻYCZEŃ:");
                    for (Rental r : history) {
                        String zwrot = (r.getReturnDateTime() != null) ? r.getReturnDateTime().toString() : "W TRAKCIE (Brak zwrotu)";
                        System.out.println("  -> Użytkownik ID: " + r.getUserId());
                        System.out.println("     Wypożyczono: " + r.getRentDateTime());
                        System.out.println("     Zwrócono:    " + zwrot);
                    }
                } else {
                    System.out.println("  -> Brak historii dla tego pojazdu.");
                }
                System.out.println("----------------------------------------------------------------------");
            }
        }
    }

    private void adminAddVehicle() {
        try {
            System.out.print("Podaj kategorię (np. Car, Motorcycle, Bus): ");
            String category = sc.nextLine();
            System.out.print("Podaj markę: ");
            String brand = sc.nextLine();
            System.out.print("Podaj model: ");
            String model = sc.nextLine();
            System.out.print("Podaj rocznik: ");
            int year = Integer.parseInt(sc.nextLine());
            System.out.print("Podaj cenę za dzień: ");
            double price = Double.parseDouble(sc.nextLine());
            System.out.print("Podaj tablicę rejestracyjną: ");
            String plate = sc.nextLine();

            Map<String, Object> attrs = new HashMap<>();
            if (category.equalsIgnoreCase("Motorcycle")) {
                System.out.print("Podaj wymaganą licencję (np. A, A2): ");
                attrs.put("licence", sc.nextLine());
            }

            Vehicle newVehicle = Vehicle.builder()
                    .category(category)
                    .brand(brand)
                    .model(model)
                    .year(year)
                    .price(price)
                    .plate(plate)
                    .attributes(attrs)
                    .build();

            vehicleRepository.save(newVehicle);
            System.out.println("Dodano pojazd.");
        } catch (Exception e) {
            System.out.println("Błąd podczas dodawania pojazdu. Upewnij się, że dane liczbowe są poprawne.");
        }
    }

    private void adminRemoveVehicle() {
        System.out.print("Podaj ID pojazdu do usunięcia: ");
        String id = sc.nextLine();

        if (rentalService.getRentalStatusForVehicle(id).equals("Wypożyczony")) {
            System.out.println("BŁĄD: Nie można usunąć pojazdu, który jest aktualnie wypożyczony!");
            return;
        }

        vehicleRepository.deleteById(id);
        System.out.println("Pojazd o ID " + id + " został usunięty z bazy.");
    }

    private void adminDisplayUsers() {
        List<User> users = userRepository.findAll();
        for (User u : users) {
            System.out.println("Użytkownik: " + u.getLogin() + " | Rola: " + u.getRole());
            if (u.getRole() == Role.USER) {
                Optional<Rental> activeRental = rentalService.getActiveRentalForUser(u.getId());
                if (activeRental.isPresent()) {
                    System.out.println(" -> Aktywne wypożyczenie: Pojazd ID " + activeRental.get().getVehicleId());
                } else {
                    System.out.println(" -> Brak aktywnego wypożyczenia.");
                }
            }
        }
    }

    private void adminRemoveUser() {
        System.out.print("Podaj ID użytkownika do usunięcia: ");
        String id = sc.nextLine();

        Optional<User> u = userRepository.findById(id);
        if (u.isPresent() && u.get().getRole() == Role.ADMIN) {
            System.out.println("Nie można usunąć konta administratora.");
            return;
        }

        if (rentalService.getActiveRentalForUser(id).isPresent()) {
            System.out.println("Nie można usunąć użytkownika, ponieważ ma aktywne wypożyczenie.");
            return;
        }

        userRepository.deleteById(id);
        System.out.println("Usunięto użytkownika (jeśli istniał).");
    }
}