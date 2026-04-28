package com.goltracker.tournament;

import com.goltracker.tournament.dto.TournamentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    @GetMapping
    public List<TournamentDto> list() {
        return tournamentService.getEnabled();
    }

    @GetMapping("/{id}")
    public TournamentDto detail(@PathVariable Long id) {
        return tournamentService.findById(id);
    }
}
