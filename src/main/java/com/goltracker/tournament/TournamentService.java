package com.goltracker.tournament;

import com.goltracker.core.exception.ApiException;
import com.goltracker.tournament.domain.Tournament;
import com.goltracker.tournament.dto.TournamentDto;
import com.goltracker.tournament.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    @Transactional(readOnly = true)
    public List<TournamentDto> getEnabled() {
        return tournamentRepository.findByEnabledTrueOrderBySortOrderAsc()
                .stream().map(TournamentDto::from).toList();
    }

    @Transactional(readOnly = true)
    public TournamentDto findById(Long id) {
        Tournament t = tournamentRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Torneo no encontrado: " + id));
        return TournamentDto.from(t);
    }
}
