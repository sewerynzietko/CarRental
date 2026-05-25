package org.example.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "rental")
public class Rental {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
    @Column(name = "rent_date")
    private LocalDateTime rentDateTime;
    @Column(name = "return_date")
    private LocalDateTime returnDateTime;

    public Rental copy(){
        return Rental.builder()
                .id(id)
                .vehicle(vehicle)
                .user(user)
                .rentDateTime(rentDateTime)
                .returnDateTime(returnDateTime)
                .build();
    }

    public boolean isActive() {
        return returnDateTime == null;
    }
}
