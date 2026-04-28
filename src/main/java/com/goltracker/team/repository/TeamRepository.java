package com.goltracker.team.repository;

import com.goltracker.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByNameIgnoreCase(String name);

    Optional<Team> findByNameIgnoreCaseAndTournamentId(String name, Long tournamentId);

    List<Team> findByGroupNameOrderByWonDescDrawnDescGoalsForDesc(String groupName);

    List<Team> findByGroupNameAndTournamentIdOrderByWonDescDrawnDescGoalsForDesc(String groupName, Long tournamentId);

    @Query("SELECT t FROM Team t ORDER BY t.groupName, t.won DESC, t.drawn DESC, t.goalsFor DESC")
    List<Team> findAllOrdered();

    @Query("SELECT t FROM Team t WHERE t.tournament.enabled = true ORDER BY t.tournament.sortOrder, t.groupName, t.won DESC, t.drawn DESC, t.goalsFor DESC")
    List<Team> findAllFromEnabledTournaments();

    @Query("SELECT t FROM Team t WHERE t.tournament.enabled = true AND t.groupName = :groupName ORDER BY t.won DESC, t.drawn DESC, t.goalsFor DESC")
    List<Team> findByGroupFromEnabledTournaments(@Param("groupName") String groupName);

    @Query("SELECT t FROM Team t WHERE t.tournament.id = :tid ORDER BY t.groupName, t.won DESC, t.drawn DESC, t.goalsFor DESC")
    List<Team> findAllByTournamentId(@Param("tid") Long tournamentId);

    boolean existsByName(String name);
}
