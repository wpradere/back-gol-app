package com.goltracker.knockout.service;

import com.goltracker.core.exception.ApiException;
import com.goltracker.knockout.domain.KnockoutConfig;
import com.goltracker.knockout.domain.KnockoutMatch;
import com.goltracker.knockout.dto.KnockoutBracketDto;
import com.goltracker.knockout.dto.KnockoutMatchDto;
import com.goltracker.knockout.repository.KnockoutConfigRepository;
import com.goltracker.knockout.repository.KnockoutMatchRepository;
import com.goltracker.team.domain.Team;
import com.goltracker.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KnockoutService {

    private final KnockoutConfigRepository configRepo;
    private final KnockoutMatchRepository matchRepo;
    private final TeamRepository teamRepo;

    // ── Public ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public KnockoutBracketDto getPublicBracket() {
        KnockoutConfig cfg = getConfig();
        if (!cfg.isPublished()) throw ApiException.notFound("Bracket no publicado");
        return buildBracket(cfg);
    }

    // ── Admin ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public KnockoutBracketDto getAdminBracket() {
        return buildBracket(getConfig());
    }

    @Transactional
    public KnockoutBracketDto toggleEnabled() {
        KnockoutConfig cfg = getConfig();
        cfg.setEnabled(!cfg.isEnabled());
        configRepo.save(cfg);
        return buildBracket(cfg);
    }

    @Transactional
    public KnockoutBracketDto togglePublished() {
        KnockoutConfig cfg = getConfig();
        cfg.setPublished(!cfg.isPublished());
        configRepo.save(cfg);
        return buildBracket(cfg);
    }

    @Transactional
    public KnockoutMatchDto setTeams(Integer matchId, Long teamAId, Long teamBId) {
        KnockoutMatch match = findMatch(matchId);
        match.setTeamA(teamAId != null ? teamRepo.findById(teamAId)
                .orElseThrow(() -> ApiException.notFound("Equipo no encontrado")) : null);
        match.setTeamB(teamBId != null ? teamRepo.findById(teamBId)
                .orElseThrow(() -> ApiException.notFound("Equipo no encontrado")) : null);
        return KnockoutMatchDto.from(matchRepo.save(match));
    }

    @Transactional
    public KnockoutMatchDto setResult(Integer matchId, Integer scoreA, Integer scoreB) {
        KnockoutMatch match = findMatch(matchId);
        if (match.getTeamA() == null || match.getTeamB() == null)
            throw ApiException.badRequest("Asigna ambos equipos antes de guardar el resultado");

        match.setScoreA(scoreA);
        match.setScoreB(scoreB);
        match.setPlayed(true);

        Team winner = null;
        if (scoreA > scoreB)  winner = match.getTeamA();
        else if (scoreB > scoreA) winner = match.getTeamB();
        match.setWinner(winner);
        matchRepo.save(match);

        // Auto-avanzar ganador
        if (winner != null && match.getNextMatchId() != null) {
            KnockoutMatch next = findMatch(match.getNextMatchId());
            if ("A".equals(match.getNextSlot())) next.setTeamA(winner);
            else                                  next.setTeamB(winner);
            matchRepo.save(next);
        }

        return KnockoutMatchDto.from(match);
    }

    @Transactional
    public KnockoutMatchDto resetResult(Integer matchId) {
        KnockoutMatch match = findMatch(matchId);
        Team prevWinner = match.getWinner();
        match.setScoreA(null);
        match.setScoreB(null);
        match.setPlayed(false);
        match.setWinner(null);
        matchRepo.save(match);

        // Retirar ganador del siguiente partido si sigue siendo ese equipo
        if (prevWinner != null && match.getNextMatchId() != null) {
            KnockoutMatch next = findMatch(match.getNextMatchId());
            if ("A".equals(match.getNextSlot())) {
                if (prevWinner.equals(next.getTeamA())) next.setTeamA(null);
            } else {
                if (prevWinner.equals(next.getTeamB())) next.setTeamB(null);
            }
            matchRepo.save(next);
        }

        return KnockoutMatchDto.from(match);
    }

    @Transactional
    public KnockoutMatchDto setKickoff(Integer matchId, LocalDateTime kickoffAt) {
        KnockoutMatch match = findMatch(matchId);
        match.setKickoffAt(kickoffAt);
        return KnockoutMatchDto.from(matchRepo.save(match));
    }

    @Transactional
    public KnockoutMatchDto lockMatch(Integer matchId) {
        KnockoutMatch match = findMatch(matchId);
        match.setPredictionsLocked(true);
        return KnockoutMatchDto.from(matchRepo.save(match));
    }

    @Transactional
    public KnockoutMatchDto unlockMatch(Integer matchId) {
        KnockoutMatch match = findMatch(matchId);
        match.setPredictionsLocked(false);
        return KnockoutMatchDto.from(matchRepo.save(match));
    }

    // ── Private ───────────────────────────────────────────────────────────────

    private KnockoutBracketDto buildBracket(KnockoutConfig cfg) {
        List<KnockoutMatchDto> matches = matchRepo.findAllWithTeams()
                .stream().map(KnockoutMatchDto::from).toList();
        return new KnockoutBracketDto(cfg.isEnabled(), cfg.isPublished(), matches);
    }

    private KnockoutConfig getConfig() {
        return configRepo.findById(1L).orElseGet(() -> {
            KnockoutConfig c = new KnockoutConfig(1L, false, false);
            return configRepo.save(c);
        });
    }

    private KnockoutMatch findMatch(Integer id) {
        return matchRepo.findById(id)
                .orElseThrow(() -> ApiException.notFound("Partido de cruces no encontrado: " + id));
    }
}
