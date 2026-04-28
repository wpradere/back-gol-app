package com.goltracker.prediction.dto;

import com.goltracker.prediction.domain.Prediction;

public record PredictionResponse(
        Long id,
        Long matchId,
        String matchDate,
        String teamAName,
        String teamAFlag,
        String teamBName,
        String teamBFlag,
        boolean matchPlayed,
        Integer realScoreA,
        Integer realScoreB,
        int predictedScoreA,
        int predictedScoreB,
        Integer points
) {
    public static PredictionResponse from(Prediction p) {
        var match = p.getMatch();
        return new PredictionResponse(
                p.getId(),
                match.getId(),
                match.getMatchDate(),
                match.getTeamA().getName(),
                match.getTeamA().getFlag(),
                match.getTeamB().getName(),
                match.getTeamB().getFlag(),
                match.isPlayed(),
                match.getScoreA(),
                match.getScoreB(),
                p.getPredictedScoreA(),
                p.getPredictedScoreB(),
                p.getPoints()
        );
    }
}
