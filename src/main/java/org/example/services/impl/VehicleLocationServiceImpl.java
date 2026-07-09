package org.example.services.impl;

import org.example.dto.LocationRequest;
import org.example.models.Rental;
import org.example.models.Vehicle;
import org.example.models.VehicleLocation;
import org.example.repositories.RentalRepository;
import org.example.repositories.VehicleLocationRepository;
import org.example.repositories.VehicleRepository;
import org.example.services.VehicleLocationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class VehicleLocationServiceImpl implements VehicleLocationService {
    private final VehicleLocationRepository locationRepository;
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;
    private final Random random = new Random();

    private final Map<String, LocalDateTime> manualOverrides = new ConcurrentHashMap<>();

    private final double hqLatitude = 51.2465;
    private final double hqLongitude = 22.5684;
    private final double allowedRadiusMeters = 50.0;

    public VehicleLocationServiceImpl(VehicleLocationRepository locationRepository, VehicleRepository vehicleRepository, RentalRepository rentalRepository) {
        this.locationRepository = locationRepository;
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    public void updateLocation(String vehicleId, LocationRequest request) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu."));

        VehicleLocation location = locationRepository.findByVehicleId(vehicleId)
                .orElse(VehicleLocation.builder().vehicle(vehicle).build());

        location.setLatitude(request.latitude());
        location.setLongitude(request.longitude());
        location.setLastUpdated(LocalDateTime.now());
        locationRepository.save(location);

        manualOverrides.put(vehicleId, LocalDateTime.now().plusMinutes(1));
    }

    @Override
    public void validateReturnLocation(String vehicleId) {
        VehicleLocation location = locationRepository.findByVehicleId(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Brak danych o lokalizacji pojazdu."));

        double distance = calculateDistance(location.getLatitude(), location.getLongitude(), hqLatitude, hqLongitude);

        if (distance > allowedRadiusMeters) {
            throw new IllegalArgumentException("Pojazd znajduje się poza dozwoloną strefą zwrotu. Odległość: " + Math.round(distance) + "m");
        }
    }

    @Scheduled(fixedRate = 10000)
    public void simulateGpsMovement() {
        List<Rental> activeRentals = rentalRepository.findAll().stream()
                .filter(Rental::isActive)
                .toList();

        LocalDateTime now = LocalDateTime.now();

        for (Rental rental : activeRentals) {
            String vehicleId = rental.getVehicle().getId();

            if (manualOverrides.containsKey(vehicleId) && now.isBefore(manualOverrides.get(vehicleId))) {
                continue;
            }

            VehicleLocation location = locationRepository.findByVehicleId(vehicleId)
                    .orElseGet(() -> VehicleLocation.builder()
                            .vehicle(rental.getVehicle())
                            .latitude(hqLatitude)
                            .longitude(hqLongitude)
                            .lastUpdated(now)
                            .build());

            double distance = 500 + random.nextDouble() * 1000;
            double bearing = random.nextDouble() * 2 * Math.PI;

            double latOffset = (distance * Math.cos(bearing)) / 111320.0;
            double lonOffset = (distance * Math.sin(bearing)) / (111320.0 * Math.cos(Math.toRadians(location.getLatitude())));

            location.setLatitude(location.getLatitude() + latOffset);
            location.setLongitude(location.getLongitude() + lonOffset);
            location.setLastUpdated(now);

            locationRepository.save(location);
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleLocation getLocation(String vehicleId) {
        return locationRepository.findByVehicleId(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono lokalizacji dla pojazdu: " + vehicleId));
    }
}