package com.goltracker.knockout.dto;

import com.goltracker.knockout.domain.KnockoutPrediction;

public record KnockoutPredictionDto(
        Long id,
        Integer matchId,
        Integer predictedScoreA,
        Integer predictedScoreB,
        Integer points
) {
    public static KnockoutPredictionDto from(KnockoutPrediction p) {
        return new KnockoutPredictionDto(
                p.getId(),
                p.getMatch().getId(),
                p.getPredictedScoreA(),
                p.getPredictedScoreB(),
                p.getPoints()
        );
    }
}
