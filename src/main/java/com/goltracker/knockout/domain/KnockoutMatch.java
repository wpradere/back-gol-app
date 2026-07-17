package com.goltracker.knockout.domain;

import com.goltracker.team.domain.Team;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "knockout_matches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KnockoutMatch {

    @Id
    private Integer id;

    @Column(nullable = false, length = 10)
    private String round;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_a_id")
    private Team teamA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_b_id")
    private Team teamB;

    @Column(name = "score_a")
    private Integer scoreA;

    @Column(name = "score_b")
    private Integer scoreB;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private Team winner;

    @Column(nullable = false)
    private boolean played = false;

    @Column(name = "kickoff_at")
    private LocalDateTime kickoffAt;

    @Column(name = "predictions_locked", nullable = false)
    private boolean predictionsLocked = false;

    @Column(name = "next_match_id")
    private Integer nextMatchId;

    @Column(name = "next_slot", length = 1)
    private String nextSlot;

    @Column(name = "loser_next_match_id")
    private Integer loserNextMatchId;

    @Column(name = "loser_next_slot", length = 1)
    private String loserNextSlot;
}
