package org.example.renting;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VehicleRepositoryImpl implements IVehicleRepository {
    List<Vehicle> vehicles;

    @Override
    public boolean rentVehicle ( String id ) {
        for( Vehicle vehicle : vehicles ){
            if(vehicle.getId().equals(id)){
                if(vehicle.isRented()){
                    return false;
                }
                vehicle.setRented(true);
                save();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean returnVehicle ( String id ) {
        for( Vehicle vehicle : vehicles ){
            if(vehicle.getId().equals(id)) {
                if(!vehicle.isRented()) {
                    return false;
                }
                vehicle.setRented(false);
                save();
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Vehicle> getVehicles ( ) {
        List<Vehicle> retVehicles = new ArrayList<>();
        for( Vehicle vehicle : vehicles){
            retVehicles.add(vehicle.cloneVehicle());
        }
        return retVehicles;
    }
    @Override
    public void save () {
        StringBuilder str = new StringBuilder();
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("vehicles.csv"))){
            for ( Vehicle vehicle : vehicles ){
                str.append(vehicle.toCsv()).append("\n");
            }
            writer.write(str.toString());
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
    }

    @Override
    public void load () {
        File file = new File("vehicles.csv");
        try(Scanner myReader = new Scanner(file)){
            while(myReader.hasNextLine()){
                String line = myReader.nextLine();
                String[] data = line.split(";");
                if(data[0].equals("CAR")){
                    Car car = new Car(
                            data[1], data[2], data[3], Integer.parseInt(data[4]),
                            Float.parseFloat(data[5]), Boolean.parseBoolean(data[6]));
                    vehicles.add(car);
                }
                else{
                    Motorcycle motorcycle = new Motorcycle(
                            data[1], data[2], data[3], Integer.parseInt(data[4]),
                            Float.parseFloat(data[5]), Boolean.parseBoolean(data[6]), data[7]);
                    vehicles.add(motorcycle);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred: " + e);
        }
    }

    @Override
    public boolean add ( Vehicle vehicle ) {
        vehicles.add(vehicle);
        save();
        return true;
    }

    @Override
    public boolean remove ( String id ) {
        boolean bool = vehicles.removeIf(vehicle -> vehicle.getId().equals(id));
        save();
        return bool;
    }

    @Override
    public Vehicle getVehicle ( String id ) {
        for( Vehicle vehicle : vehicles ) {
            if (vehicle.getId().equals(id)) {
                return vehicle;
            }
        }
        return null;
    }

    public VehicleRepositoryImpl ( ) {
        vehicles = new ArrayList<>();
        load();
    }
}
