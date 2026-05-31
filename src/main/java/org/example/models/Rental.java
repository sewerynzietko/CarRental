package org.example.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rental")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String vehicleId;

    @Column(nullable = false)
    private String userId;

    @Column(name = "rent_date", nullable = false)
    private LocalDateTime rentDateTime;

    @Column(name = "return_date")
    private LocalDateTime returnDateTime;

    public Rental copy(){
        return Rental.builder()
                .id(id)
                .vehicleId(vehicleId)
                .userId(userId)
                .rentDateTime(rentDateTime)
                .returnDateTime(returnDateTime)
                .build();
    }

    public boolean isActive() {
        return returnDateTime == null;
    }
}
