package com.goltracker.auth.dto;

public record AuthResponse(
        String token,
        String username,
        String role,
        String status,
        boolean requiresPasswordReset
) {}
