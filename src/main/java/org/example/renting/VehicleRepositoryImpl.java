package org.example.renting;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VehicleRepositoryImpl implements IVehicleRepository {
    List<Vehicle> vehicles;

    @Override
    public void rentVehicle ( String id ) {
        for( Vehicle vehicle : vehicles ){
            if(vehicle.getId().equals(id)){
                if(!vehicle.isRented()){
                    vehicle.setRented(true);
                    save("vehicles.csv");
                }
                break;
            }
        }
    }

    @Override
    public void returnVehicle ( String id ) {
        for( Vehicle vehicle : vehicles ){
            if(vehicle.getId().equals(id)) {
                if(vehicle.isRented()) {
                    vehicle.setRented(false);
                    save("vehicles.csv");
                    return;
                }
            }
        }
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
    public void save ( String filename ) {
        StringBuilder str = new StringBuilder();
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filename))){
            for ( Vehicle vehicle : vehicles ){
                str.append(vehicle.toCsv()).append("\n");
            }
            writer.write(str.toString());
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
    }

    @Override
    public void load ( String filename ) {
        File file = new File(filename);
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
    public void add ( Vehicle vehicle ) {
        vehicles.add(vehicle);
    }

    @Override
    public void remove ( String id ) {
        vehicles.removeIf(vehicle -> vehicle.getId().equals(id));
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
        load("vehicles.csv");
    }
}
