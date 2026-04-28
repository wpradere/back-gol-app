package com.goltracker.match;

import com.goltracker.match.dto.MatchDto;
import com.goltracker.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    // GET /api/matches                      → todos los partidos
    // GET /api/matches?group=A             → filtro por grupo
    // GET /api/matches?tournamentId=1      → filtro por torneo
    @GetMapping
    public List<MatchDto> list(
            @RequestParam(required = false) String group,
            @RequestParam(required = false) Long tournamentId) {
        return matchService.findAll(group, tournamentId);
    }

    // GET /api/matches/{id}
    @GetMapping("/{id}")
    public MatchDto detail(@PathVariable Long id) {
        return matchService.findById(id);
    }
}
