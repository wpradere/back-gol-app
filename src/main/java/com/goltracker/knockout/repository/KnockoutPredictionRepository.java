package com.goltracker.knockout.repository;

import com.goltracker.knockout.domain.KnockoutPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KnockoutPredictionRepository extends JpaRepository<KnockoutPrediction, Long> {

    @Query("SELECT p FROM KnockoutPrediction p JOIN FETCH p.match m LEFT JOIN FETCH m.teamA LEFT JOIN FETCH m.teamB WHERE p.user.username = :username")
    List<KnockoutPrediction> findByUsername(@Param("username") String username);

    Optional<KnockoutPrediction> findByUserUsernameAndMatchId(String username, Integer matchId);

    List<KnockoutPrediction> findByMatchId(Integer matchId);
}
