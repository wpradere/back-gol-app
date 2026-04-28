package com.goltracker.tournament.repository;

import com.goltracker.tournament.domain.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    List<Tournament> findByEnabledTrueOrderBySortOrderAsc();
    List<Tournament> findAllByOrderBySortOrderAsc();
    Optional<Tournament> findByShortNameIgnoreCase(String shortName);
}
