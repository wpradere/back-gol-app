package com.goltracker.admin;

import com.goltracker.admin.dto.ImportResult;
import com.goltracker.admin.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/import")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    @PostMapping("/torneo")
    public ImportResult importTorneo(@RequestParam("file") MultipartFile file) {
        return importService.importTorneo(file);
    }

    @PostMapping("/teams")
    public ImportResult importTeams(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long tournamentId) {
        return importService.importTeams(file, tournamentId);
    }

    @PostMapping("/matches")
    public ImportResult importMatches(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long tournamentId) {
        return importService.importMatches(file, tournamentId);
    }

    /** Importa equipos desde un CSV que incluye torneo_id en cada fila. */
    @PostMapping("/teams/csv")
    public ImportResult importTeamsCsv(@RequestParam("file") MultipartFile file) {
        return importService.importTeamsCsv(file);
    }

    /** Importa partidos desde un CSV que incluye torneo_id en cada fila. */
    @PostMapping("/matches/csv")
    public ImportResult importMatchesCsv(@RequestParam("file") MultipartFile file) {
        return importService.importMatchesCsv(file);
    }

    /** Importa jugadores desde un CSV con torneo_corto y equipo_nombre por fila. */
    @PostMapping("/players")
    public ImportResult importPlayers(@RequestParam("file") MultipartFile file) {
        return importService.importPlayers(file);
    }
}
