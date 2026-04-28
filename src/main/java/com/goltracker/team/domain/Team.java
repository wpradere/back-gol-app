package com.goltracker.team.domain;

import com.goltracker.tournament.domain.Tournament;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams",
        uniqueConstraints = @UniqueConstraint(name = "uq_team_name_tournament", columnNames = {"name", "tournament_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 10)
    private String flag;

    @Column(length = 20)
    private String confederation;

    @Column(name = "group_name", length = 2)
    private String groupName;

    private int played;
    private int won;
    private int drawn;
    private int lost;
    private int goalsFor;
    private int goalsAgainst;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Player> players = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Scorer> scorers = new ArrayList<>();

    public int getPoints() {
        return won * 3 + drawn;
    }

    public int getGoalDiff() {
        return goalsFor - goalsAgainst;
    }
}
