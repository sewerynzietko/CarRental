package org.example.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_location")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehicleLocation {
    @Id
    private String vehicleId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;
}