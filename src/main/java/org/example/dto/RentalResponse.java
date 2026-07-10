package org.example.dto;

import org.example.models.Rental;
import java.time.LocalDateTime;

public record RentalResponse(
        String id,
        String vehicleId,
        String vehicleName,
        LocalDateTime rentDateTime,
        LocalDateTime returnDateTime,
        String paymentStatus
) {
    public static RentalResponse fromEntity(Rental rental) {
        String status = (rental.getPayment() != null && rental.getPayment().getStatus() != null)
                ? rental.getPayment().getStatus().name()
                : "BRAK";

        return new RentalResponse(
                rental.getId(),
                rental.getVehicle().getId(),
                rental.getVehicle().getBrand() + " " + rental.getVehicle().getModel(),
                rental.getRentDateTime(),
                rental.getReturnDateTime(),
                status
        );
    }
}