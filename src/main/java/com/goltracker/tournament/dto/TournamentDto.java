package com.goltracker.tournament.dto;

import com.goltracker.tournament.domain.Tournament;

public record TournamentDto(
        Long id,
        String name,
        String shortName,
        String icon,
        String season,
        boolean enabled,
        int sortOrder
) {
    public static TournamentDto from(Tournament t) {
        return new TournamentDto(
                t.getId(), t.getName(), t.getShortName(),
                t.getIcon(), t.getSeason(), t.isEnabled(), t.getSortOrder()
        );
    }
}
