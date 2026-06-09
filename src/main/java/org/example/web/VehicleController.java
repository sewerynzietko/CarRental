package org.example.web;

import org.example.dto.VehicleRequest;
import org.example.models.Vehicle;
import org.example.services.VehicleServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    private final VehicleServiceInterface vehicleService;

    public VehicleController ( VehicleServiceInterface vehicleService ) {
        this.vehicleService = vehicleService;
    }
    @GetMapping
    public List<Vehicle> list(
            @RequestParam(name = "available", required = false, defaultValue = "false")
            boolean available
    ) {
        return available
                ? vehicleService.findAvailableVehicles()
                : vehicleService.findAllVehicles();
    }
    @GetMapping("/id")
    public ResponseEntity<Vehicle> get( @RequestBody VehicleRequest vehicleRequest ) {
        return ResponseEntity.status(HttpStatus.OK).body(vehicleService.findById(vehicleRequest.vehicleId()));
    }
    @PostMapping
    public Vehicle create(@RequestBody Vehicle vehicle) {
        return vehicleService.addVehicle(vehicle);
    }
    @DeleteMapping("/id")
    public ResponseEntity<Void> delete(@RequestBody VehicleRequest vehicleRequest) {
        vehicleService.removeVehicle(vehicleRequest.vehicleId());
        return ResponseEntity.noContent().build();
    }
}