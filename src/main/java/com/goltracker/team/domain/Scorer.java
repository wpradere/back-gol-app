package com.goltracker.team.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scorers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Scorer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int goals;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
}
