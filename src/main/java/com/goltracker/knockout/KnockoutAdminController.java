package com.goltracker.knockout;

import com.goltracker.admin.dto.MatchKickoffRequest;
import com.goltracker.knockout.dto.KnockoutBracketDto;
import com.goltracker.knockout.dto.KnockoutMatchDto;
import com.goltracker.knockout.service.KnockoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/knockout")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class KnockoutAdminController {

    private final KnockoutService knockoutService;

    @GetMapping
    public KnockoutBracketDto getBracket() {
        return knockoutService.getAdminBracket();
    }

    @PostMapping("/toggle-enabled")
    public KnockoutBracketDto toggleEnabled() {
        return knockoutService.toggleEnabled();
    }

    /** Publish a specific phase (R16, R8, R4, SEMI, FINAL) — validates all teams are assigned. */
    @PostMapping("/publish-phase/{round}")
    public KnockoutBracketDto publishPhase(@PathVariable String round) {
        return knockoutService.publishPhase(round);
    }

    /** Unpublish a specific phase. */
    @PostMapping("/unpublish-phase/{round}")
    public KnockoutBracketDto unpublishPhase(@PathVariable String round) {
        return knockoutService.unpublishPhase(round);
    }

    @PutMapping("/matches/{id}/teams")
    public KnockoutMatchDto setTeams(
            @PathVariable Integer id,
            @RequestBody Map<String, Long> body) {
        return knockoutService.setTeams(id, body.get("teamAId"), body.get("teamBId"));
    }

    @PutMapping("/matches/{id}/result")
    public KnockoutMatchDto setResult(
            @PathVariable Integer id,
            @RequestBody Map<String, Integer> body) {
        return knockoutService.setResult(id, body.get("scoreA"), body.get("scoreB"));
    }

    @DeleteMapping("/matches/{id}/result")
    public KnockoutMatchDto resetResult(@PathVariable Integer id) {
        return knockoutService.resetResult(id);
    }

    /** Manually assign the winner of a drawn match (extra time / penalties). */
    @PutMapping("/matches/{id}/winner")
    public KnockoutMatchDto setWinner(
            @PathVariable Integer id,
            @RequestBody Map<String, Long> body) {
        return knockoutService.setWinner(id, body.get("winnerId"));
    }

    @PutMapping("/matches/{id}/kickoff")
    public KnockoutMatchDto setKickoff(
            @PathVariable Integer id,
            @RequestBody MatchKickoffRequest request) {
        return knockoutService.setKickoff(id, request.kickoffAt());
    }

    @PutMapping("/matches/{id}/lock")
    public KnockoutMatchDto lockMatch(@PathVariable Integer id) {
        return knockoutService.lockMatch(id);
    }

    @PutMapping("/matches/{id}/unlock")
    public KnockoutMatchDto unlockMatch(@PathVariable Integer id) {
        return knockoutService.unlockMatch(id);
    }
}
