package org.example.dto;

public record RegisterRequest(
        String login,
        String password,
        String city,
        String street,
        String postalCode
) {}

