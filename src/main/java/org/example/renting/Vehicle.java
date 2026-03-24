package org.example.renting;

public abstract class Vehicle {
    protected String id;
    protected String brand;
    protected String model;
    protected int year;
    protected float price;
    protected boolean rented;

    public String getId () {
        return id;
    }

    public boolean isRented (){
        return this.rented;
    }

    public void setRented ( boolean rent ){
        this.rented = rent;
    }

    public Vehicle(String id, String brand, String model, int year, float price, boolean rented) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.rented = rented;
    }

    public abstract String toCsv ();

    public abstract String toString ();

    public abstract Vehicle cloneVehicle();
}
