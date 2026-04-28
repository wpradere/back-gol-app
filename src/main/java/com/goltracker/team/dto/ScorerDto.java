package com.goltracker.team.dto;

import com.goltracker.team.domain.Scorer;

public record ScorerDto(Long id, String name, int goals) {
    public static ScorerDto from(Scorer s) {
        return new ScorerDto(s.getId(), s.getName(), s.getGoals());
    }
}
