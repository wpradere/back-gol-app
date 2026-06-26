package com.goltracker.knockout;

import com.goltracker.knockout.dto.KnockoutBracketDto;
import com.goltracker.knockout.dto.KnockoutMatchDto;
import com.goltracker.knockout.dto.KnockoutPredictionDto;
import com.goltracker.knockout.service.KnockoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/knockout")
@RequiredArgsConstructor
public class KnockoutController {

    private final KnockoutService knockoutService;

    /** Full bracket (only when any phase is published). */
    @GetMapping
    public KnockoutBracketDto getBracket() {
        return knockoutService.getPublicBracket();
    }

    /** Knockout matches open for predictions (published, not yet played). */
    @GetMapping("/matches/published")
    public List<KnockoutMatchDto> getPublishedMatches() {
        return knockoutService.getPublishedMatches();
    }

    /** Knockout matches that are published AND already played (for historial). */
    @GetMapping("/matches/played")
    public List<KnockoutMatchDto> getPlayedMatches() {
        return knockoutService.getPlayedPublishedMatches();
    }

    /** Current user's knockout predictions. */
    @GetMapping("/predictions")
    public List<KnockoutPredictionDto> myPredictions(@AuthenticationPrincipal UserDetails user) {
        return knockoutService.getUserPredictions(user.getUsername());
    }

    /** Create or update a knockout prediction. */
    @PutMapping("/predictions/{matchId}")
    public KnockoutPredictionDto upsertPrediction(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Integer matchId,
            @RequestBody Map<String, Integer> body) {
        return knockoutService.upsertPrediction(
                user.getUsername(), matchId,
                body.get("scoreA"), body.get("scoreB"));
    }
}
