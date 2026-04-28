package com.goltracker.tournament.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tournaments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "short_name", nullable = false, length = 30)
    private String shortName;

    @Column(length = 10)
    private String icon;

    @Column(length = 20)
    private String season;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = false;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private int sortOrder = 0;
}
