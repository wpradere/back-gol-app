package com.goltracker.admin;

import com.goltracker.admin.dto.*;
import com.goltracker.admin.service.AdminService;
import com.goltracker.team.dto.TeamSummaryDto;
import com.goltracker.tournament.dto.TournamentDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ── Partidos ───────────────────────────────────────────────────────────

    @GetMapping("/matches")
    public List<AdminMatchDto> listMatches() {
        return adminService.getAllMatches();
    }

    /** Bloquea manualmente las predicciones de un partido. */
    @PutMapping("/matches/{id}/lock")
    public AdminMatchDto lockMatch(@PathVariable Long id) {
        return adminService.lockMatch(id);
    }

    /** Desbloquea las predicciones (solo si el partido no fue jugado). */
    @PutMapping("/matches/{id}/unlock")
    public AdminMatchDto unlockMatch(@PathVariable Long id) {
        return adminService.unlockMatch(id);
    }

    /** Configura la fecha/hora exacta de inicio para el auto-lock. */
    @PutMapping("/matches/{id}/kickoff")
    public AdminMatchDto setKickoff(
            @PathVariable Long id,
            @Valid @RequestBody MatchKickoffRequest request) {
        return adminService.setKickoff(id, request);
    }

    /** Registra el resultado final y recalcula puntos de predicciones. */
    @PutMapping("/matches/{id}/result")
    public AdminMatchDto saveResult(
            @PathVariable Long id,
            @Valid @RequestBody MatchResultRequest request) {
        return adminService.saveResult(id, request);
    }

    /** Limpia el resultado: vuelve el partido a estado no jugado. */
    @PutMapping("/matches/{id}/reset-result")
    public AdminMatchDto resetResult(@PathVariable Long id) {
        return adminService.resetResult(id);
    }

    @DeleteMapping("/matches/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMatch(@PathVariable Long id) {
        adminService.deleteMatch(id);
    }

    // ── Equipos ────────────────────────────────────────────────────────────

    @GetMapping("/teams")
    public List<TeamSummaryDto> listTeams() {
        return adminService.getAllTeams();
    }

    @PutMapping("/teams/{id}/stats")
    public TeamSummaryDto updateTeamStats(
            @PathVariable Long id,
            @Valid @RequestBody TeamUpdateRequest request) {
        return adminService.updateTeamStats(id, request);
    }

    @DeleteMapping("/teams/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeam(@PathVariable Long id) {
        adminService.deleteTeam(id);
    }

    // ── Torneos ────────────────────────────────────────────────────────────

    @GetMapping("/tournaments")
    public List<TournamentDto> listTournaments() {
        return adminService.getAllTournaments();
    }

    @PostMapping("/tournaments")
    @ResponseStatus(HttpStatus.CREATED)
    public TournamentDto createTournament(@Valid @RequestBody TournamentCreateRequest request) {
        return adminService.createTournament(request);
    }

    @PutMapping("/tournaments/{id}/enable")
    public TournamentDto enableTournament(@PathVariable Long id) {
        return adminService.setTournamentEnabled(id, true);
    }

    @PutMapping("/tournaments/{id}/disable")
    public TournamentDto disableTournament(@PathVariable Long id) {
        return adminService.setTournamentEnabled(id, false);
    }

    // ── Usuarios ───────────────────────────────────────────────────────────

    @GetMapping("/users")
    public List<UserSummaryDto> listUsers() {
        return adminService.getAllUsers();
    }

    /** Solicitudes pendientes de aprobación. */
    @GetMapping("/users/pending")
    public List<UserSummaryDto> listPending() {
        return adminService.getPendingUsers();
    }

    @PutMapping("/users/{id}/approve")
    public UserSummaryDto approve(@PathVariable Long id) {
        return adminService.approveUser(id);
    }

    @PutMapping("/users/{id}/reject")
    public UserSummaryDto reject(
            @PathVariable Long id,
            @RequestBody(required = false) RejectRequest request) {
        String reason = (request != null) ? request.reason() : null;
        return adminService.rejectUser(id, reason);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
    }

    /** Fuerza al usuario a cambiar su contraseña en el próximo login. */
    @PutMapping("/users/{id}/force-reset")
    public UserSummaryDto forcePasswordReset(@PathVariable Long id) {
        return adminService.forcePasswordReset(id);
    }
}
