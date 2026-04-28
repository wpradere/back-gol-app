package com.goltracker.prediction.dto;

import jakarta.validation.constraints.Min;

public record PredictionRequest(
        @Min(value = 0, message = "El marcador no puede ser negativo") int scoreA,
        @Min(value = 0, message = "El marcador no puede ser negativo") int scoreB
) {}
