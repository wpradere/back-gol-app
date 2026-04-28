package com.goltracker.team.repository;

import com.goltracker.team.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByTeamId(Long teamId);

    Optional<Player> findByTeamIdAndNameIgnoreCase(Long teamId, String name);

    void deleteByTeamId(Long teamId);
}
