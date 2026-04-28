package com.goltracker.team.dto;

import com.goltracker.team.domain.Team;

import java.util.List;

public record TeamDetailDto(
        Long id,
        String name,
        String flag,
        String confederation,
        String groupName,
        int played,
        int won,
        int drawn,
        int lost,
        int goalsFor,
        int goalsAgainst,
        int goalDiff,
        int points,
        List<ScorerDto> scorers,
        List<PlayerDto> players
) {
    public static TeamDetailDto from(Team t) {
        return new TeamDetailDto(
                t.getId(), t.getName(), t.getFlag(), t.getConfederation(),
                t.getGroupName(), t.getPlayed(), t.getWon(), t.getDrawn(),
                t.getLost(), t.getGoalsFor(), t.getGoalsAgainst(),
                t.getGoalDiff(), t.getPoints(),
                t.getScorers().stream().map(ScorerDto::from).toList(),
                t.getPlayers().stream().map(PlayerDto::from).toList()
        );
    }
}
