package com.goltracker.admin.dto;

import com.goltracker.match.domain.Match;

import java.time.LocalDateTime;

public record AdminMatchDto(
        Long id,
        String groupName,
        String matchDate,
        LocalDateTime kickoffAt,
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
    public static AdminMatchDto from(Match m) {
        return new AdminMatchDto(
                m.getId(),
                m.getGroupName(),
                m.getMatchDate(),
                m.getKickoffAt(),
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
