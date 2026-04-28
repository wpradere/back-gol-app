package com.goltracker.match.repository;

import com.goltracker.match.domain.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

    @Query("SELECT m FROM Match m JOIN FETCH m.teamA JOIN FETCH m.teamB ORDER BY m.matchDate")
    List<Match> findAllWithTeams();

    @Query("SELECT m FROM Match m JOIN FETCH m.teamA JOIN FETCH m.teamB WHERE m.tournament.enabled = true ORDER BY m.tournament.sortOrder, m.matchDate")
    List<Match> findAllFromEnabledTournaments();

    @Query("SELECT m FROM Match m JOIN FETCH m.teamA JOIN FETCH m.teamB WHERE m.tournament.enabled = true AND m.groupName = :group ORDER BY m.matchDate")
    List<Match> findByGroupFromEnabledTournaments(@Param("group") String group);

    @Query("SELECT m FROM Match m JOIN FETCH m.teamA JOIN FETCH m.teamB WHERE m.tournament.id = :tid ORDER BY m.matchDate")
    List<Match> findAllByTournamentWithTeams(@Param("tid") Long tournamentId);

    @Query("SELECT m FROM Match m JOIN FETCH m.teamA JOIN FETCH m.teamB WHERE m.groupName = :group ORDER BY m.matchDate")
    List<Match> findByGroupWithTeams(@Param("group") String group);

    @Query("SELECT m FROM Match m JOIN FETCH m.teamA JOIN FETCH m.teamB WHERE m.tournament.id = :tid AND m.groupName = :group ORDER BY m.matchDate")
    List<Match> findByTournamentAndGroupWithTeams(@Param("tid") Long tournamentId, @Param("group") String group);

    /** Partidos que aún no están bloqueados y cuyo kickoff ya pasó el umbral (now + 5 min). */
    @Query("SELECT m FROM Match m WHERE m.predictionsLocked = false AND m.kickoffAt IS NOT NULL AND m.kickoffAt <= :threshold")
    List<Match> findToAutoLock(LocalDateTime threshold);

    boolean existsByTeamAIdAndTeamBIdAndMatchDate(Long teamAId, Long teamBId, String matchDate);

    Optional<Match> findByTeamAIdAndTeamBIdAndMatchDate(Long teamAId, Long teamBId, String matchDate);

    boolean existsByTeamAIdOrTeamBId(Long teamAId, Long teamBId);
}
