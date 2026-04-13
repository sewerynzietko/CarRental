package org.example.models;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Rental {
    private String id;
    private String vehicleId;
    private String userId;
    private LocalDateTime rentDateTime;
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
