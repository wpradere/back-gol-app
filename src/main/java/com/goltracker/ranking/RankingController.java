package com.goltracker.ranking;

import com.goltracker.ranking.dto.RankingEntryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    // GET /api/ranking                    → ranking global (todos los torneos)
    // GET /api/ranking?tournamentId=1     → ranking filtrado por torneo
    @GetMapping
    public List<RankingEntryDto> ranking(
            @RequestParam(required = false) Long tournamentId) {
        return rankingService.getRanking(tournamentId);
    }
}
