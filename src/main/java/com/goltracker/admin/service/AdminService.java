package com.goltracker.admin.service;

import com.goltracker.admin.dto.*;
import com.goltracker.core.email.EmailService;
import com.goltracker.core.exception.ApiException;
import com.goltracker.match.domain.Match;
import com.goltracker.match.repository.MatchRepository;
import com.goltracker.prediction.repository.PredictionRepository;
import com.goltracker.team.domain.Team;
import com.goltracker.team.dto.TeamSummaryDto;
import com.goltracker.team.repository.TeamRepository;
import com.goltracker.tournament.domain.Tournament;
import com.goltracker.tournament.dto.TournamentDto;
import com.goltracker.tournament.repository.TournamentRepository;
import com.goltracker.user.domain.User;
import com.goltracker.user.domain.UserStatus;
import com.goltracker.user.repository.UserRepository;
import com.goltracker.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final MatchRepository      matchRepository;
    private final TeamRepository       teamRepository;
    private final UserRepository       userRepository;
    private final UserService          userService;
    private final PredictionRepository predictionRepository;
    private final TournamentRepository tournamentRepository;
    private final EmailService         emailService;

    @Value("${gol-tracker.app.url:https://auguriofutbolero.com}")
    private String appUrl;

    // ── Partidos ───────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<AdminMatchDto> getAllMatches() {
        return matchRepository.findAllFromEnabledTournaments()
                .stream()
                .map(AdminMatchDto::from)
                .toList();
    }

    @Transactional
    public AdminMatchDto lockMatch(Long matchId) {
        Match match = getMatch(matchId);
        match.setPredictionsLocked(true);
        log.info("Partido {} bloqueado manualmente.", matchId);
        return AdminMatchDto.from(matchRepository.save(match));
    }

    @Transactional
    public AdminMatchDto unlockMatch(Long matchId) {
        Match match = getMatch(matchId);
        if (match.isPlayed()) {
            throw ApiException.badRequest("No se puede desbloquear un partido ya jugado");
        }
        match.setPredictionsLocked(false);
        log.info("Partido {} desbloqueado.", matchId);
        return AdminMatchDto.from(matchRepository.save(match));
    }

    @Transactional
    public AdminMatchDto setKickoff(Long matchId, MatchKickoffRequest request) {
        Match match = getMatch(matchId);
        match.setKickoffAt(request.kickoffAt());
        // If the new kickoff is more than 5 min away, reopen predictions automatically
        if (request.kickoffAt().isAfter(LocalDateTime.now().plusMinutes(5))) {
            match.setPredictionsLocked(false);
        }
        return AdminMatchDto.from(matchRepository.save(match));
    }

    /**
     * Registra el resultado de un partido y recalcula puntos de todas las predicciones asociadas.
     */
    @Transactional
    public AdminMatchDto saveResult(Long matchId, MatchResultRequest request) {
        Match match = getMatch(matchId);
        match.setScoreA(request.scoreA());
        match.setScoreB(request.scoreB());
        match.setPlayed(true);
        match.setPredictionsLocked(true);
        matchRepository.save(match);

        // Recalcular puntos en todas las predicciones de este partido
        var predictions = predictionRepository.findByMatchId(matchId);
        predictions.forEach(p -> {
            p.recalculatePoints();
            predictionRepository.save(p);
        });
        log.info("Resultado partido {}: {} - {}. {} predicciones recalculadas.",
                matchId, request.scoreA(), request.scoreB(), predictions.size());

        return AdminMatchDto.from(match);
    }

    @Transactional
    public AdminMatchDto resetResult(Long matchId) {
        Match match = getMatch(matchId);
        match.setScoreA(null);
        match.setScoreB(null);
        match.setPlayed(false);
        match.setPredictionsLocked(false);
        match.setKickoffAt(null);   // evita que el auto-lock lo vuelva a cerrar
        matchRepository.save(match);

        // Limpiar puntos de las predicciones asociadas
        var predictions = predictionRepository.findByMatchId(matchId);
        predictions.forEach(p -> {
            p.setPoints(null);
            predictionRepository.save(p);
        });
        log.info("Resultado del partido {} limpiado. Predicciones reabiertas.", matchId);
        return AdminMatchDto.from(match);
    }

    @Transactional
    public void deleteMatch(Long matchId) {
        if (!matchRepository.existsById(matchId)) {
            throw ApiException.notFound("Partido no encontrado: " + matchId);
        }
        matchRepository.deleteById(matchId);
    }

    // ── Equipos ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<TeamSummaryDto> getAllTeams() {
        return teamRepository.findAllFromEnabledTournaments()
                .stream()
                .map(TeamSummaryDto::from)
                .toList();
    }

    @Transactional
    public TeamSummaryDto updateTeamStats(Long teamId, TeamUpdateRequest request) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> ApiException.notFound("Equipo no encontrado: " + teamId));
        team.setPlayed(request.played());
        team.setWon(request.won());
        team.setDrawn(request.drawn());
        team.setLost(request.lost());
        team.setGoalsFor(request.goalsFor());
        team.setGoalsAgainst(request.goalsAgainst());
        return TeamSummaryDto.from(teamRepository.save(team));
    }

    @Transactional
    public void deleteTeam(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw ApiException.notFound("Equipo no encontrado: " + teamId);
        }
        if (matchRepository.existsByTeamAIdOrTeamBId(teamId, teamId)) {
            throw ApiException.badRequest("No se puede eliminar el equipo porque tiene partidos asociados. Eliminá los partidos primero.");
        }
        teamRepository.deleteById(teamId);
    }

    // ── Usuarios ───────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<UserSummaryDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserSummaryDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserSummaryDto> getPendingUsers() {
        return userRepository.findByStatus(UserStatus.PENDING_APPROVAL)
                .stream()
                .map(UserSummaryDto::from)
                .toList();
    }

    @Transactional
    public UserSummaryDto approveUser(Long userId) {
        userService.approveUser(userId);
        return UserSummaryDto.from(userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("Usuario no encontrado")));
    }

    @Transactional
    public UserSummaryDto rejectUser(Long userId, String reason) {
        userService.rejectUser(userId, reason);
        return UserSummaryDto.from(userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("Usuario no encontrado")));
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw ApiException.notFound("Usuario no encontrado: " + userId);
        }
        userRepository.deleteById(userId);
    }

    // ── Torneos ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<TournamentDto> getAllTournaments() {
        return tournamentRepository.findAllByOrderBySortOrderAsc()
                .stream().map(TournamentDto::from).toList();
    }

    @Transactional
    public TournamentDto createTournament(TournamentCreateRequest request) {
        int sortOrder = (int) tournamentRepository.count() + 1;
        Tournament t = Tournament.builder()
                .name(request.name())
                .shortName(request.shortName())
                .icon(autoIcon(request.name()))
                .season(request.season())
                .sortOrder(sortOrder)
                .build();
        log.info("Torneo creado: {} ({}) con ícono {}", t.getName(), t.getShortName(), t.getIcon());
        return TournamentDto.from(tournamentRepository.save(t));
    }

    private String autoIcon(String name) {
        String lower = name.toLowerCase();
        if (lower.contains("mundial") || lower.contains("world cup")) return "🌍";
        if (lower.contains("copa") || lower.contains("cup"))          return "🏆";
        if (lower.contains("champions"))                               return "⭐";
        if (lower.contains("euro"))                                    return "🇪🇺";
        if (lower.contains("liga") || lower.contains("league"))       return "⚽";
        if (lower.contains("africa") || lower.contains("chan"))        return "🌍";
        if (lower.contains("america"))                                 return "🌎";
        if (lower.contains("asia"))                                    return "🌏";
        return "🏆";
    }

    @Transactional
    public TournamentDto setTournamentEnabled(Long id, boolean enabled) {
        Tournament t = tournamentRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Torneo no encontrado: " + id));
        t.setEnabled(enabled);
        log.info("Torneo {} {} por admin.", t.getName(), enabled ? "habilitado" : "deshabilitado");
        return TournamentDto.from(tournamentRepository.save(t));
    }

    // ── Usuarios ───────────────────────────────────────────────────────────

    @Transactional
    public UserSummaryDto forcePasswordReset(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("Usuario no encontrado: " + userId));

        String resetToken = UUID.randomUUID().toString();
        String code       = String.format("%06d", new Random().nextInt(1_000_000));

        user.setForcePasswordReset(true);
        user.setResetToken(resetToken);
        user.setResetCode(code);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.saveAndFlush(user); // flush garantiza que el token está en BD antes de enviar el correo

        String link = appUrl + "/reset-password?token=" + resetToken;
        emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), code, link);

        log.info("[Admin] Reset forzado para '{}': token={}, expiry={}",
                user.getUsername(), resetToken, user.getResetTokenExpiry());
        return UserSummaryDto.from(user);
    }

    // ── Auto-lock (llamado desde el scheduler) ─────────────────────────────

    @Transactional
    public int autoLockDueMatches() {
        LocalDateTime threshold = LocalDateTime.now().plusMinutes(5);
        List<Match> tolock = matchRepository.findToAutoLock(threshold);
        tolock.forEach(m -> m.setPredictionsLocked(true));
        matchRepository.saveAll(tolock);
        if (!tolock.isEmpty()) {
            log.info("Auto-lock: {} partido(s) bloqueados.", tolock.size());
        }
        return tolock.size();
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private Match getMatch(Long matchId) {
        return matchRepository.findById(matchId)
                .orElseThrow(() -> ApiException.notFound("Partido no encontrado: " + matchId));
    }
}
