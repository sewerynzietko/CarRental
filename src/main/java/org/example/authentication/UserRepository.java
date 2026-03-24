package org.example.authentication;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserRepository implements IUserRepository{
    private ArrayList<User> users;

    public UserRepository () {
        users = new ArrayList<>();
        load();
    }

    @Override
    public User getUser (String login) {
        for(User user : users){
            if(user.getLogin().equals(login)){
                return new User(user);
            }
        }
        return null;
    }

    @Override
    public List<User> getUsers () {
        return new ArrayList<>(users);
    }

    @Override
    public void save ( ) {
        StringBuilder str = new StringBuilder();
        try(BufferedWriter writer = new BufferedWriter
                (new FileWriter("users.csv"))){
            for ( User user : users ){
                str.append(user.toCsv()).append("\n");
            }
            writer.write(str.toString());
            System.out.println("Użytkownicy zapisani.");
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
    }

    @Override
    public void load ( ) {
        File file = new File("users.csv");
        try(Scanner myReader = new Scanner(file)){
            while(myReader.hasNextLine()) {
                String line = myReader.nextLine();
                String[] data = line.split(";");
                Role role;
                if (data[2].equals("USER")) role = Role.USER;
                else role = Role.ADMIN;
                User user = new User(data[0], data[1], role);
                if (data.length == 4) user.setRentedVehicleId(data[3]);
                users.add(user);
            }
            System.out.println("Użytkownicy załadowani.");
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred: " + e);
        }
    }

    @Override
    public boolean update () {
        return false; //update aktualizuje pojazd w repo bo getuser zwraca kopie
    }

}
