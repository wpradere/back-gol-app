package com.goltracker.admin.dto;

import jakarta.validation.constraints.Min;

public record TeamUpdateRequest(
        @Min(0) int played,
        @Min(0) int won,
        @Min(0) int drawn,
        @Min(0) int lost,
        @Min(0) int goalsFor,
        @Min(0) int goalsAgainst
) {}
