package com.goltracker.auth.dto;

public record RegisterResponse(
        String message,
        String username,
        String email
) {}
