package org.example.renting;

public class Motorcycle extends Vehicle {
    private String category;

    public Motorcycle ( String id, String brand, String model, int year, float price, boolean rented, String category ) {
        super(id, brand, model, year, price, rented);
        this.category = category;
    }

    public Motorcycle (Motorcycle motorcycle){
        super(motorcycle.id, motorcycle.brand, motorcycle.model,
                motorcycle.year, motorcycle.price, motorcycle.rented);
        this.category = motorcycle.category;
    }

    @Override
    public String toCsv(){
        return "MOTORCYCLE" + ';' + id + ';' + brand + ';' + model + ';' + year
                + ';' + price + ';' + rented + ';' + category;
    }

    @Override
    public String toString() {
        return "Motorcycle: " +
                "id:" + id +
                ", rented:" + rented +
                ", category:" + category +
                ", brand:" + brand +
                ", model:" + model +
                ", year:" + year +
                ", price:" + price;
    }

    @Override
    public Vehicle cloneVehicle() {
        return new Motorcycle(this);
    }
}
