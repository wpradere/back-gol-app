package com.goltracker.ranking.dto;

public record RankingEntryDto(
        int rank,
        String username,
        long totalPoints,
        long predicted,
        long exactCount,
        long correctCount
) {}
