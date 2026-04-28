package com.goltracker.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record TournamentCreateRequest(
        @NotBlank String name,
        @NotBlank String shortName,
        String season
) {}
