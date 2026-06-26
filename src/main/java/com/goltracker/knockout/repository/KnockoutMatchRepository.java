package com.goltracker.knockout.repository;

import com.goltracker.knockout.domain.KnockoutMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KnockoutMatchRepository extends JpaRepository<KnockoutMatch, Integer> {

    @Query("SELECT m FROM KnockoutMatch m LEFT JOIN FETCH m.teamA LEFT JOIN FETCH m.teamB LEFT JOIN FETCH m.winner ORDER BY m.id")
    List<KnockoutMatch> findAllWithTeams();
}
