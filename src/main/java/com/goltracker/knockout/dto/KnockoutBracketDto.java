package com.goltracker.knockout.dto;

import java.util.List;

public record KnockoutBracketDto(
        boolean enabled,
        boolean published,
        boolean r16Published,
        boolean r8Published,
        boolean r4Published,
        boolean semiPublished,
        boolean finalPublished,
        List<KnockoutMatchDto> matches
) {}
