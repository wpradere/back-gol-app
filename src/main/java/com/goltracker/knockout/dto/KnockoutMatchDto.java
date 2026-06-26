package com.goltracker.knockout.dto;

import com.goltracker.knockout.domain.KnockoutMatch;
import java.time.LocalDateTime;

public record KnockoutMatchDto(
        Integer id,
        String round,
        int sortOrder,
        Long teamAId,
        String teamAName,
        String teamAFlag,
        Long teamBId,
        String teamBName,
        String teamBFlag,
        Integer scoreA,
        Integer scoreB,
        Long winnerId,
        String winnerName,
        boolean played,
        LocalDateTime kickoffAt,
        boolean predictionsLocked,
        Integer nextMatchId,
        String nextSlot
) {
    public static KnockoutMatchDto from(KnockoutMatch m) {
        return new KnockoutMatchDto(
                m.getId(),
                m.getRound(),
                m.getSortOrder(),
                m.getTeamA() != null ? m.getTeamA().getId() : null,
                m.getTeamA() != null ? m.getTeamA().getName() : null,
                m.getTeamA() != null ? m.getTeamA().getFlag() : null,
                m.getTeamB() != null ? m.getTeamB().getId() : null,
                m.getTeamB() != null ? m.getTeamB().getName() : null,
                m.getTeamB() != null ? m.getTeamB().getFlag() : null,
                m.getScoreA(),
                m.getScoreB(),
                m.getWinner() != null ? m.getWinner().getId() : null,
                m.getWinner() != null ? m.getWinner().getName() : null,
                m.isPlayed(),
                m.getKickoffAt(),
                m.isPredictionsLocked(),
                m.getNextMatchId(),
                m.getNextSlot()
        );
    }
}
