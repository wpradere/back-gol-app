package com.goltracker.match.service;

import com.goltracker.core.exception.ApiException;
import com.goltracker.match.domain.Match;
import com.goltracker.match.dto.MatchDto;
import com.goltracker.match.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {

    private final MatchRepository matchRepository;

    public List<MatchDto> findAll(String group, Long tournamentId) {
        List<Match> matches;
        if (tournamentId != null) {
            matches = (group != null && !group.isBlank())
                    ? matchRepository.findByTournamentAndGroupWithTeams(tournamentId, group.toUpperCase())
                    : matchRepository.findAllByTournamentWithTeams(tournamentId);
        } else {
            // Sin filtro de torneo → solo partidos de torneos habilitados
            matches = (group != null && !group.isBlank())
                    ? matchRepository.findByGroupFromEnabledTournaments(group.toUpperCase())
                    : matchRepository.findAllFromEnabledTournaments();
        }
        return matches.stream().map(MatchDto::from).toList();
    }

    public MatchDto findById(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Partido no encontrado: " + id));
        return MatchDto.from(match);
    }
}
