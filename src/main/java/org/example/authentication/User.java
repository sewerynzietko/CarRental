package org.example.authentication;

public class User {
    private String login;
    private String passwordHash;
    private Role role;
    private String rentedVehicleId;

    public User ( String login, String passwordHash, Role role ) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public User (User user){
        this.login = user.login;
        this.passwordHash = user.passwordHash;
        this.role = user.role;
        this.rentedVehicleId = user.rentedVehicleId;
    }
    public String getLogin () {
        return login;
    }

    public String getPasswordHash () {
        return passwordHash;
    }

    public Role getRole () {
        return role;
    }

    public String getRentedVehicleId () {
        return rentedVehicleId;
    }

    public void setRentedVehicleId ( String rentedVehicleId ) {
        this.rentedVehicleId = rentedVehicleId;
    }

    public String toCsv(){
        String vehicleIdStr = (rentedVehicleId != null) ? rentedVehicleId : "";
        return login + ';' + passwordHash +
                ';' + role + ';' + vehicleIdStr;
    }
}
