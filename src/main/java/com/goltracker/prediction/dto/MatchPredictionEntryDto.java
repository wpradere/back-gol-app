package com.goltracker.prediction.dto;

public record MatchPredictionEntryDto(
        String username,
        int predictedScoreA,
        int predictedScoreB,
        Integer points
) {}
