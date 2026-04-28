package com.goltracker.team.service;

import com.goltracker.core.exception.ApiException;
import com.goltracker.team.domain.Team;
import com.goltracker.team.dto.TeamDetailDto;
import com.goltracker.team.dto.TeamSummaryDto;
import com.goltracker.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;

    public List<TeamSummaryDto> findAll(String group, Long tournamentId) {
        List<Team> teams;
        if (tournamentId != null) {
            teams = (group != null && !group.isBlank())
                    ? teamRepository.findByGroupNameAndTournamentIdOrderByWonDescDrawnDescGoalsForDesc(group.toUpperCase(), tournamentId)
                    : teamRepository.findAllByTournamentId(tournamentId);
        } else {
            // Sin filtro de torneo → solo equipos de torneos habilitados
            teams = (group != null && !group.isBlank())
                    ? teamRepository.findByGroupFromEnabledTournaments(group.toUpperCase())
                    : teamRepository.findAllFromEnabledTournaments();
        }
        return teams.stream().map(TeamSummaryDto::from).toList();
    }

    public TeamDetailDto findByName(String name) {
        Team team = teamRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> ApiException.notFound("Equipo no encontrado: " + name));
        return TeamDetailDto.from(team);
    }

    public TeamDetailDto findById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Equipo no encontrado: " + id));
        return TeamDetailDto.from(team);
    }
}
