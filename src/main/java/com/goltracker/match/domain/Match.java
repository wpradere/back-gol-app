package com.goltracker.match.domain;

import com.goltracker.team.domain.Team;
import com.goltracker.tournament.domain.Tournament;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name", length = 3)
    private String groupName;

    @Column(name = "match_date", length = 30)
    private String matchDate;

    /** Fecha y hora exacta de inicio (para el bloqueo automático 5 min antes). */
    @Column(name = "kickoff_at")
    private LocalDateTime kickoffAt;

    @Column(length = 100)
    private String stadium;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_a_id")
    private Team teamA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_b_id")
    private Team teamB;

    private boolean played;

    @Column(name = "score_a")
    private Integer scoreA;

    @Column(name = "score_b")
    private Integer scoreB;

    /** Cuando está en true no se aceptan nuevas predicciones para este partido. */
    @Builder.Default
    @Column(name = "predictions_locked", nullable = false)
    private boolean predictionsLocked = false;
}
