package com.goltracker.knockout.repository;

import com.goltracker.knockout.domain.KnockoutMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface KnockoutMatchRepository extends JpaRepository<KnockoutMatch, Integer> {

    @Query("SELECT m FROM KnockoutMatch m LEFT JOIN FETCH m.teamA LEFT JOIN FETCH m.teamB LEFT JOIN FETCH m.winner ORDER BY m.id")
    List<KnockoutMatch> findAllWithTeams();

    @Query("SELECT m FROM KnockoutMatch m LEFT JOIN FETCH m.teamA LEFT JOIN FETCH m.teamB LEFT JOIN FETCH m.winner WHERE m.round = :round ORDER BY m.sortOrder")
    List<KnockoutMatch> findByRoundWithTeams(@Param("round") String round);

    @Query("SELECT m FROM KnockoutMatch m LEFT JOIN FETCH m.teamA LEFT JOIN FETCH m.teamB LEFT JOIN FETCH m.winner WHERE m.round IN :rounds ORDER BY m.id")
    List<KnockoutMatch> findByRoundsWithTeams(@Param("rounds") List<String> rounds);

    @Query("SELECT m FROM KnockoutMatch m WHERE m.predictionsLocked = false AND m.played = false AND m.kickoffAt IS NOT NULL AND m.kickoffAt <= :threshold")
    List<KnockoutMatch> findToAutoLock(@Param("threshold") LocalDateTime threshold);
}
