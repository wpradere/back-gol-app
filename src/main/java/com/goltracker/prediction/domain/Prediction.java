package com.goltracker.prediction.domain;

import com.goltracker.match.domain.Match;
import com.goltracker.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "predictions",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_prediction_user_match",
                columnNames = {"user_id", "match_id"}
        )
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(name = "predicted_score_a")
    private int predictedScoreA;

    @Column(name = "predicted_score_b")
    private int predictedScoreB;

    // Calculado al momento de guardar si el partido ya fue jugado
    private Integer points;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ── Lógica de puntuación ───────────────────────────────────────────────
    //
    // +2 pts: ganador correcto (o empate)
    // +1 pt por cada puntaje predicho que aparezca en los goles reales (multiset)
    // Máximo posible: 4 pts (marcador exacto: 2 + 1 + 1)

    public void recalculatePoints() {
        if (!match.isPlayed()) {
            this.points = null;
            return;
        }
        int rA = match.getScoreA();
        int rB = match.getScoreB();
        int total = 0;

        // 1. Ganador correcto
        String predWinner = predictedScoreA > predictedScoreB ? "A"
                : predictedScoreA < predictedScoreB ? "B" : "D";
        String realWinner = rA > rB ? "A" : rA < rB ? "B" : "D";
        if (predWinner.equals(realWinner)) total += 2;

        // 2. Matching de puntajes (multiset): +1 por cada valor predicho que
        //    aparezca en los goles reales, independientemente del equipo.
        java.util.List<Integer> remaining = new java.util.ArrayList<>(java.util.List.of(rA, rB));
        for (int pv : new int[]{predictedScoreA, predictedScoreB}) {
            if (remaining.remove(Integer.valueOf(pv))) {
                total += 1;
            }
        }

        this.points = total;
    }
}
