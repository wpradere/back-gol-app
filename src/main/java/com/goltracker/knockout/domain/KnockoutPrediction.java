package com.goltracker.knockout.domain;

import com.goltracker.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "knockout_predictions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KnockoutPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private KnockoutMatch match;

    @Column(name = "predicted_score_a", nullable = false)
    private Integer predictedScoreA;

    @Column(name = "predicted_score_b", nullable = false)
    private Integer predictedScoreB;

    private Integer points;
}
