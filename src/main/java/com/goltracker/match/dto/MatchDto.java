package com.goltracker.match.dto;

import com.goltracker.match.domain.Match;

public record MatchDto(
        Long id,
        String groupName,
        String matchDate,
        String kickoffDate,
        String stadium,
        String teamAName,
        String teamAFlag,
        String teamBName,
        String teamBFlag,
        boolean played,
        Integer scoreA,
        Integer scoreB,
        boolean predictionsLocked
) {
    public static MatchDto from(Match m) {
        return new MatchDto(
                m.getId(),
                m.getGroupName(),
                m.getMatchDate(),
                m.getKickoffAt() != null ? m.getKickoffAt().toLocalDate().toString() : null,
                m.getStadium(),
                m.getTeamA() != null ? m.getTeamA().getName() : "TBD",
                m.getTeamA() != null ? m.getTeamA().getFlag() : "",
                m.getTeamB() != null ? m.getTeamB().getName() : "TBD",
                m.getTeamB() != null ? m.getTeamB().getFlag() : "",
                m.isPlayed(),
                m.getScoreA(),
                m.getScoreB(),
                m.isPredictionsLocked()
        );
    }
}
