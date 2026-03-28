package org.example.renting;
public class Car extends Vehicle {
    public Car ( String id, String brand, String model, int year, float price, boolean rented ) {
        super(id, brand, model, year, price, rented);
    }

    public Car (Car car){
        super(car.id, car.brand, car.model,
                car.year, car.price, car.rented);
    }
    @Override
    public String toCsv(){
        return "CAR" + ';' + super.id + ';' + brand + ';' + model + ';' + year
                + ';' + price + ';' + rented;
    }

    @Override
    public String toString() {
        return "Car: " +
                "id: " + id +
                ", rented: " + rented +
                ", price: " + price +
                ", year: " + year +
                ", model: " + model +
                ", brand: " + brand;
    }

    @Override
    public Vehicle cloneVehicle() {
        return new Car(this);
    }

}
