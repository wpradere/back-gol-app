package com.goltracker.admin.dto;

import jakarta.validation.constraints.Min;

public record MatchResultRequest(
        @Min(value = 0, message = "El marcador no puede ser negativo") int scoreA,
        @Min(value = 0, message = "El marcador no puede ser negativo") int scoreB
) {}
