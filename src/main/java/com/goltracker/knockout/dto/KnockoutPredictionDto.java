package com.goltracker.knockout.dto;

import com.goltracker.knockout.domain.KnockoutMatch;
import com.goltracker.knockout.domain.KnockoutPrediction;

public record KnockoutPredictionDto(
        Long id,
        Integer matchId,
        String round,
        String teamAName,
        String teamAFlag,
        String teamBName,
        String teamBFlag,
        Integer scoreA,
        Integer scoreB,
        boolean played,
        Integer predictedScoreA,
        Integer predictedScoreB,
        Integer points
) {
    public static KnockoutPredictionDto from(KnockoutPrediction p) {
        KnockoutMatch m = p.getMatch();
        return new KnockoutPredictionDto(
                p.getId(),
                m.getId(),
                m.getRound(),
                m.getTeamA() != null ? m.getTeamA().getName() : null,
                m.getTeamA() != null ? m.getTeamA().getFlag() : null,
                m.getTeamB() != null ? m.getTeamB().getName() : null,
                m.getTeamB() != null ? m.getTeamB().getFlag() : null,
                m.getScoreA(),
                m.getScoreB(),
                m.isPlayed(),
                p.getPredictedScoreA(),
                p.getPredictedScoreB(),
                p.getPoints()
        );
    }
}
