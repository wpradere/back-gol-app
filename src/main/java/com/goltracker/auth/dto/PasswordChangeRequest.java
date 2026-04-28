package com.goltracker.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordChangeRequest(
        @NotBlank String changeToken,
        @NotBlank @Size(min = 8) String newPassword
) {}
