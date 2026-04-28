package com.goltracker.team.dto;

import com.goltracker.team.domain.Player;

public record PlayerDto(
        Long id,
        String name,
        int number,
        String position,
        int age,
        String club,
        int games,
        int goals,
        int assists,
        boolean isStarter
) {
    public static PlayerDto from(Player p) {
        return new PlayerDto(
                p.getId(), p.getName(), p.getNumber(), p.getPosition(),
                p.getAge(), p.getClub(), p.getGames(), p.getGoals(),
                p.getAssists(), p.isStarter()
        );
    }
}
