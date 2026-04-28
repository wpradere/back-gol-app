package com.goltracker.prediction.service;

import com.goltracker.core.exception.ApiException;
import com.goltracker.match.domain.Match;
import com.goltracker.match.repository.MatchRepository;
import com.goltracker.prediction.domain.Prediction;
import com.goltracker.prediction.dto.PredictionRequest;
import com.goltracker.prediction.dto.PredictionResponse;
import com.goltracker.prediction.dto.PredictionSummaryDto;
import com.goltracker.prediction.repository.PredictionRepository;
import com.goltracker.user.domain.User;
import com.goltracker.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final MatchRepository matchRepository;
    private final UserService userService;

    @PreAuthorize("#username == authentication.name")
    @Transactional(readOnly = true)
    public List<PredictionResponse> findAllForUser(String username) {
        User user = userService.findByUsername(username);
        return predictionRepository.findByUserId(user.getId())
                .stream()
                .map(PredictionResponse::from)
                .toList();
    }

    @PreAuthorize("#username == authentication.name")
    @Transactional
    public PredictionResponse upsert(String username, Long matchId, PredictionRequest request) {
        User user  = userService.findByUsername(username);
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> ApiException.notFound("Partido no encontrado: " + matchId));

        if (match.isPlayed()) {
            throw ApiException.badRequest("No se puede predecir un partido ya jugado");
        }
        if (match.isPredictionsLocked()) {
            throw ApiException.badRequest("Las predicciones para este partido están cerradas");
        }

        Prediction prediction = predictionRepository
                .findByUserIdAndMatchId(user.getId(), matchId)
                .orElse(Prediction.builder().user(user).match(match).build());

        prediction.setPredictedScoreA(request.scoreA());
        prediction.setPredictedScoreB(request.scoreB());
        prediction.recalculatePoints();

        return PredictionResponse.from(predictionRepository.save(prediction));
    }

    @PreAuthorize("#username == authentication.name")
    @Transactional(readOnly = true)
    public PredictionSummaryDto summary(String username) {
        User user = userService.findByUsername(username);
        List<Prediction> predictions = predictionRepository.findByUserId(user.getId());

        int totalMatches = (int) matchRepository.count();
        int predicted    = predictions.size();
        int totalPoints  = predictions.stream()
                .filter(p -> p.getPoints() != null)
                .mapToInt(Prediction::getPoints)
                .sum();
        int exact = (int) predictions.stream()
                .filter(p -> p.getPoints() != null && p.getPoints() == 4)
                .count();
        int correct = (int) predictions.stream()
                .filter(p -> p.getPoints() != null && p.getPoints() >= 2)
                .count();

        return new PredictionSummaryDto(totalMatches, predicted, totalPoints, exact, correct);
    }
}
