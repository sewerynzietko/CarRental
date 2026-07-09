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

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "rent_date", nullable = false)
    private LocalDateTime rentDateTime;

    @Column(name = "return_date")
    private LocalDateTime returnDateTime;

    @OneToOne(
            mappedBy = "rental",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    private Payment payment;

    public Rental copy() {
        return Rental.builder()
                .id(id)
                .vehicle(vehicle)
                .user(user)
                .rentDateTime(rentDateTime)
                .returnDateTime(returnDateTime)
                .payment(payment)
                .build();
    }

    public boolean isActive() {
        return returnDateTime == null;
    }
}