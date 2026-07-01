package com.goltracker.knockout.service;

import com.goltracker.core.exception.ApiException;
import com.goltracker.knockout.domain.KnockoutConfig;
import com.goltracker.knockout.domain.KnockoutMatch;
import com.goltracker.knockout.domain.KnockoutPrediction;
import com.goltracker.knockout.dto.KnockoutBracketDto;
import com.goltracker.knockout.dto.KnockoutMatchDto;
import com.goltracker.knockout.dto.KnockoutPredictionDto;
import com.goltracker.knockout.repository.KnockoutConfigRepository;
import com.goltracker.knockout.repository.KnockoutMatchRepository;
import com.goltracker.knockout.repository.KnockoutPredictionRepository;
import com.goltracker.team.domain.Team;
import com.goltracker.team.repository.TeamRepository;
import com.goltracker.user.domain.User;
import com.goltracker.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KnockoutService {

    private final KnockoutConfigRepository configRepo;
    private final KnockoutMatchRepository matchRepo;
    private final KnockoutPredictionRepository predictionRepo;
    private final TeamRepository teamRepo;
    private final UserService userService;

    // ── Public ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public KnockoutBracketDto getPublicBracket() {
        KnockoutConfig cfg = getConfig();
        if (!cfg.isEnabled()) throw ApiException.notFound("Bracket no disponible");
        // Returns bracket only if at least one phase is published
        boolean anyPublished = cfg.isR16Published() || cfg.isR8Published() ||
                cfg.isR4Published() || cfg.isSemiPublished() || cfg.isFinalPublished();
        if (!anyPublished) throw ApiException.notFound("Bracket no publicado");
        return buildBracket(cfg);
    }

    /** Knockout matches open for predictions — published and not yet played. */
    @Transactional(readOnly = true)
    public List<KnockoutMatchDto> getPublishedMatches() {
        KnockoutConfig cfg = getConfig();
        List<String> publishedRounds = getPublishedRounds(cfg);
        if (publishedRounds.isEmpty()) return List.of();
        return matchRepo.findByRoundsWithTeams(publishedRounds)
                .stream()
                .filter(m -> !m.isPlayed())
                .map(KnockoutMatchDto::from)
                .toList();
    }

    /** Knockout matches that are published AND already played (for historial). */
    @Transactional(readOnly = true)
    public List<KnockoutMatchDto> getPlayedPublishedMatches() {
        KnockoutConfig cfg = getConfig();
        List<String> publishedRounds = getPublishedRounds(cfg);
        if (publishedRounds.isEmpty()) return List.of();
        return matchRepo.findByRoundsWithTeams(publishedRounds)
                .stream()
                .filter(KnockoutMatch::isPlayed)
                .map(KnockoutMatchDto::from)
                .toList();
    }

    // ── Predictions ───────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<KnockoutPredictionDto> getUserPredictions(String username) {
        return predictionRepo.findByUsername(username)
                .stream()
                .map(KnockoutPredictionDto::from)
                .toList();
    }

    /** Played knockout predictions for public profile view (ranking/[username] page). */
    @Transactional(readOnly = true)
    public List<KnockoutPredictionDto> getPlayedUserPredictions(String username) {
        return predictionRepo.findByUsername(username)
                .stream()
                .filter(p -> p.getMatch().isPlayed())
                .map(KnockoutPredictionDto::from)
                .toList();
    }

    /** All users' predictions for a specific knockout match (for the ranking match selector). */
    @Transactional(readOnly = true)
    public List<com.goltracker.prediction.dto.MatchPredictionEntryDto> getMatchPredictions(Integer matchId) {
        return predictionRepo.findByMatchId(matchId)
                .stream()
                .map(p -> new com.goltracker.prediction.dto.MatchPredictionEntryDto(
                        p.getUser().getUsername(),
                        p.getPredictedScoreA(),
                        p.getPredictedScoreB(),
                        p.getPoints()
                ))
                .toList();
    }

    @Transactional
    public KnockoutPredictionDto upsertPrediction(String username, Integer matchId,
                                                   Integer scoreA, Integer scoreB) {
        KnockoutMatch match = findMatch(matchId);
        KnockoutConfig cfg = getConfig();

        if (!isPhasePublished(cfg, match.getRound())) {
            throw ApiException.badRequest("Esta fase no está publicada para predicciones");
        }
        if (match.isPlayed()) {
            throw ApiException.badRequest("El partido ya fue jugado");
        }
        if (match.isPredictionsLocked()) {
            throw ApiException.badRequest("Las predicciones para este partido están cerradas");
        }

        User user = userService.findByUsername(username);
        KnockoutPrediction pred = predictionRepo
                .findByUserUsernameAndMatchId(username, matchId)
                .orElseGet(KnockoutPrediction::new);

        pred.setUser(user);
        pred.setMatch(match);
        pred.setPredictedScoreA(scoreA);
        pred.setPredictedScoreB(scoreB);
        pred.setPoints(null);

        return KnockoutPredictionDto.from(predictionRepo.save(pred));
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

    /** Publish a specific phase for user predictions. Validates all team slots are filled. */
    @Transactional
    public KnockoutBracketDto publishPhase(String round) {
        List<KnockoutMatch> matches = matchRepo.findByRoundWithTeams(round);
        List<String> missing = new ArrayList<>();
        for (KnockoutMatch m : matches) {
            if (m.getTeamA() == null) missing.add("Partido #" + m.getId() + " falta Equipo A");
            if (m.getTeamB() == null) missing.add("Partido #" + m.getId() + " falta Equipo B");
        }
        if (!missing.isEmpty()) {
            throw ApiException.badRequest("No se puede publicar " + round +
                    " — faltan equipos: " + String.join(", ", missing));
        }

        KnockoutConfig cfg = getConfig();
        setPhasePublished(cfg, round, true);
        // When any phase is published, mark global published=true for bracket visibility
        cfg.setPublished(true);
        configRepo.save(cfg);
        return buildBracket(cfg);
    }

    @Transactional
    public KnockoutBracketDto unpublishPhase(String round) {
        KnockoutConfig cfg = getConfig();
        setPhasePublished(cfg, round, false);
        // Recalculate global published
        boolean anyPublished = cfg.isR16Published() || cfg.isR8Published() ||
                cfg.isR4Published() || cfg.isSemiPublished() || cfg.isFinalPublished();
        cfg.setPublished(anyPublished);
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
        if (scoreA > scoreB)      winner = match.getTeamA();
        else if (scoreB > scoreA) winner = match.getTeamB();
        match.setWinner(winner);
        matchRepo.save(match);

        // Auto-advance winner to next match
        if (winner != null && match.getNextMatchId() != null) {
            KnockoutMatch next = findMatch(match.getNextMatchId());
            if ("A".equals(match.getNextSlot())) next.setTeamA(winner);
            else                                  next.setTeamB(winner);
            matchRepo.save(next);
        }

        // Recalculate prediction points for this match
        recalculatePredictionPoints(match, scoreA, scoreB);

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

        // Remove winner from next match if it's still that team
        if (prevWinner != null && match.getNextMatchId() != null) {
            KnockoutMatch next = findMatch(match.getNextMatchId());
            if ("A".equals(match.getNextSlot())) {
                if (prevWinner.equals(next.getTeamA())) next.setTeamA(null);
            } else {
                if (prevWinner.equals(next.getTeamB())) next.setTeamB(null);
            }
            matchRepo.save(next);
        }

        // Reset prediction points
        predictionRepo.findByMatchId(matchId).forEach(p -> {
            p.setPoints(null);
            predictionRepo.save(p);
        });

        return KnockoutMatchDto.from(match);
    }

    @Transactional
    public int autoLockDueMatches() {
        LocalDateTime threshold = LocalDateTime.now().plusMinutes(30);
        List<KnockoutMatch> tolock = matchRepo.findToAutoLock(threshold);
        tolock.forEach(m -> m.setPredictionsLocked(true));
        matchRepo.saveAll(tolock);
        return tolock.size();
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
        return new KnockoutBracketDto(
                cfg.isEnabled(),
                cfg.isPublished(),
                cfg.isR16Published(),
                cfg.isR8Published(),
                cfg.isR4Published(),
                cfg.isSemiPublished(),
                cfg.isFinalPublished(),
                matches
        );
    }

    private KnockoutConfig getConfig() {
        return configRepo.findById(1L).orElseGet(() -> {
            KnockoutConfig c = new KnockoutConfig();
            c.setId(1L);
            return configRepo.save(c);
        });
    }

    private KnockoutMatch findMatch(Integer id) {
        return matchRepo.findById(id)
                .orElseThrow(() -> ApiException.notFound("Partido de cruces no encontrado: " + id));
    }

    private List<String> getPublishedRounds(KnockoutConfig cfg) {
        List<String> rounds = new ArrayList<>();
        if (cfg.isR16Published())   rounds.add("R16");
        if (cfg.isR8Published())    rounds.add("R8");
        if (cfg.isR4Published())    rounds.add("R4");
        if (cfg.isSemiPublished())  rounds.add("SEMI");
        if (cfg.isFinalPublished()) rounds.add("FINAL");
        return rounds;
    }

    private boolean isPhasePublished(KnockoutConfig cfg, String round) {
        return switch (round) {
            case "R16"   -> cfg.isR16Published();
            case "R8"    -> cfg.isR8Published();
            case "R4"    -> cfg.isR4Published();
            case "SEMI"  -> cfg.isSemiPublished();
            case "FINAL" -> cfg.isFinalPublished();
            default      -> false;
        };
    }

    private void setPhasePublished(KnockoutConfig cfg, String round, boolean value) {
        switch (round) {
            case "R16"   -> cfg.setR16Published(value);
            case "R8"    -> cfg.setR8Published(value);
            case "R4"    -> cfg.setR4Published(value);
            case "SEMI"  -> cfg.setSemiPublished(value);
            case "FINAL" -> cfg.setFinalPublished(value);
        }
    }

    private void recalculatePredictionPoints(KnockoutMatch match, int scoreA, int scoreB) {
        predictionRepo.findByMatchId(match.getId()).forEach(pred -> {
            int pA = pred.getPredictedScoreA(), pB = pred.getPredictedScoreB();
            int pts = 0;
            String predWinner = pA > pB ? "A" : pA < pB ? "B" : "D";
            String realWinner = scoreA > scoreB ? "A" : scoreA < scoreB ? "B" : "D";
            if (predWinner.equals(realWinner)) pts += 2;
            if (pA == scoreA) pts += 1;
            if (pB == scoreB) pts += 1;
            pred.setPoints(pts);
            predictionRepo.save(pred);
        });
    }
}
