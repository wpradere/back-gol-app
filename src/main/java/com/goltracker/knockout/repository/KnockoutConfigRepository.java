package com.goltracker.knockout.repository;

import com.goltracker.knockout.domain.KnockoutConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnockoutConfigRepository extends JpaRepository<KnockoutConfig, Long> {
}
