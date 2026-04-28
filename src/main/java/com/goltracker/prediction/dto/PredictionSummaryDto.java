package com.goltracker.prediction.dto;

public record PredictionSummaryDto(
        int totalMatches,
        int predicted,
        int totalPoints,
        int exactScores,
        int correctResults
) {}
