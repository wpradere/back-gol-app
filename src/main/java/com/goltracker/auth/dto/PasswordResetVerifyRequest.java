package com.goltracker.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordResetVerifyRequest(
        @NotBlank String token,
        @NotBlank @Pattern(regexp = "\\d{6}") String code
) {}
