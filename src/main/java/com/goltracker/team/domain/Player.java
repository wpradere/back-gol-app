package com.goltracker.team.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "players")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int number;

    @Column(length = 3)
    private String position;   // POR, DEF, MED, DEL

    private int age;
    private String club;
    private int games;
    private int goals;
    private int assists;
    private boolean isStarter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
}
