package org.example.repositories;

import org.example.models.Role;
import org.example.models.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserJsonRepository implements UserRepository {
    private ArrayList<User> users;

    public UserJsonRepository () {
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
        List<User> copies = new ArrayList<>();
        for(User u : users) {
            copies.add(new User(u));
        }
        return copies;
    }

    public void save ( ) {
        StringBuilder str = new StringBuilder();
        try(BufferedWriter writer = new BufferedWriter
                (new FileWriter("users.csv"))){
            for ( User user : users ){
                str.append(user.toCsv()).append("\n");
            }
            writer.write(str.toString());
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
    }

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
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred: " + e);
        }
    }

    @Override
    public boolean update (User updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getLogin().equals(updatedUser.getLogin())) {
                users.set(i, updatedUser);
                save();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addUser (User user){
        boolean bool = users.add(user);
        save();
        return bool;
    }

    public boolean removeUser(User user) {
        boolean bool = users.removeIf(u -> u.getLogin().equals(user.getLogin()));
        save();
        return bool;
    }
}