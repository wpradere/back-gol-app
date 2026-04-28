package com.goltracker.team.dto;

import com.goltracker.team.domain.Team;

public record TeamSummaryDto(
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
        int points
) {
    public static TeamSummaryDto from(Team t) {
        return new TeamSummaryDto(
                t.getId(), t.getName(), t.getFlag(), t.getConfederation(),
                t.getGroupName(), t.getPlayed(), t.getWon(), t.getDrawn(),
                t.getLost(), t.getGoalsFor(), t.getGoalsAgainst(), t.getPoints()
        );
    }
}
