package com.goltracker.prediction;

import com.goltracker.prediction.dto.MatchPredictionEntryDto;
import com.goltracker.prediction.dto.PredictionRequest;
import com.goltracker.prediction.dto.PredictionResponse;
import com.goltracker.prediction.dto.PredictionSummaryDto;
import com.goltracker.prediction.service.PredictionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/predictions")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;

    // GET /api/predictions  → todas las predicciones del usuario autenticado
    @GetMapping
    public List<PredictionResponse> list(@AuthenticationPrincipal UserDetails user) {
        return predictionService.findAllForUser(user.getUsername());
    }

    // PUT /api/predictions/{matchId}  → crear o actualizar predicción
    @PutMapping("/{matchId}")
    public PredictionResponse upsert(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long matchId,
            @Valid @RequestBody PredictionRequest request
    ) {
        return predictionService.upsert(user.getUsername(), matchId, request);
    }

    // GET /api/predictions/summary  → resumen de puntos del usuario
    @GetMapping("/summary")
    public PredictionSummaryDto summary(@AuthenticationPrincipal UserDetails user) {
        return predictionService.summary(user.getUsername());
    }

    // GET /api/predictions/match/{matchId}  → pronósticos de todos los jugadores para un partido
    @GetMapping("/match/{matchId}")
    public List<MatchPredictionEntryDto> byMatch(@PathVariable Long matchId) {
        return predictionService.findAllForMatch(matchId);
    }

    // GET /api/predictions/user/{username}  → historial de predicciones de un jugador
    @GetMapping("/user/{username}")
    public List<PredictionResponse> byUser(@PathVariable String username) {
        return predictionService.findAllForUserPublic(username);
    }
}
