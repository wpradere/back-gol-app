package com.goltracker.admin.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record MatchKickoffRequest(
        @NotNull(message = "La fecha/hora de inicio es obligatoria")
        LocalDateTime kickoffAt
) {}
