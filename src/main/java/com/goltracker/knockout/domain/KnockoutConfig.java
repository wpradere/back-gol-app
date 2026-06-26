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
}
