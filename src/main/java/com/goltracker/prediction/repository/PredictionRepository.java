package com.goltracker.prediction.repository;

import com.goltracker.prediction.domain.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    @Query("SELECT p FROM Prediction p JOIN FETCH p.match m JOIN FETCH m.teamA JOIN FETCH m.teamB WHERE p.user.id = :userId")
    List<Prediction> findByUserId(Long userId);

    @Query("SELECT p FROM Prediction p JOIN FETCH p.match m JOIN FETCH m.teamA JOIN FETCH m.teamB WHERE p.user.id = :userId AND m.tournament.id = :tournamentId")
    List<Prediction> findByUserIdAndTournamentId(Long userId, Long tournamentId);

    Optional<Prediction> findByUserIdAndMatchId(Long userId, Long matchId);

    List<Prediction> findByMatchId(Long matchId);

    @Query("SELECT p FROM Prediction p JOIN FETCH p.user WHERE p.match.id = :matchId")
    List<Prediction> findByMatchIdWithUser(Long matchId);

    @Query("SELECT COALESCE(SUM(p.points), 0) FROM Prediction p WHERE p.user.id = :userId AND p.points IS NOT NULL")
    int sumPointsByUserId(Long userId);
}
