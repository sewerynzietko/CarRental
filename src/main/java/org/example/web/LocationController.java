package org.example.web;

import org.example.dto.LocationRequest;
import org.example.models.VehicleLocation;
import org.example.services.VehicleLocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles/{vehicleId}/location")
public class LocationController {
    private final VehicleLocationService locationService;

    public LocationController(VehicleLocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<Void> updateLocation(@PathVariable String vehicleId, @RequestBody LocationRequest request) {
        locationService.updateLocation(vehicleId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<VehicleLocation> getLocation(@PathVariable String vehicleId) {
        return ResponseEntity.ok(locationService.getLocation(vehicleId));
    }
}