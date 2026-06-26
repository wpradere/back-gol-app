package com.goltracker.knockout.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "knockout_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KnockoutConfig {

    @Id
    private Long id;

    @Column(nullable = false)
    private boolean enabled = false;

    @Column(nullable = false)
    private boolean published = false;

    @Column(name = "r16_published", nullable = false)
    private boolean r16Published = false;

    @Column(name = "r8_published", nullable = false)
    private boolean r8Published = false;

    @Column(name = "r4_published", nullable = false)
    private boolean r4Published = false;

    @Column(name = "semi_published", nullable = false)
    private boolean semiPublished = false;

    @Column(name = "final_published", nullable = false)
    private boolean finalPublished = false;
}
