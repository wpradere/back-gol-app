package com.goltracker.team;

import com.goltracker.team.dto.TeamDetailDto;
import com.goltracker.team.dto.TeamSummaryDto;
import com.goltracker.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    // GET /api/teams                        → todos los equipos
    // GET /api/teams?group=A               → filtro por grupo
    // GET /api/teams?tournamentId=1        → filtro por torneo
    @GetMapping
    public List<TeamSummaryDto> list(
            @RequestParam(required = false) String group,
            @RequestParam(required = false) Long tournamentId) {
        return teamService.findAll(group, tournamentId);
    }

    // GET /api/teams/{name}   → detalle por nombre (ej. "Argentina")
    @GetMapping("/{name}")
    public TeamDetailDto detail(@PathVariable String name) {
        return teamService.findByName(name);
    }

    // GET /api/teams/id/{id}  → detalle por ID
    @GetMapping("/id/{id}")
    public TeamDetailDto detailById(@PathVariable Long id) {
        return teamService.findById(id);
    }
}
