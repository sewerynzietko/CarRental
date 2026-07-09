package org.example.dto;

public record AddressRequest(
        String city,
        String street,
        String postalCode
) {}