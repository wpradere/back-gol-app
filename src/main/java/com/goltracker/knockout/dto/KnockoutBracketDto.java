package com.goltracker.knockout.dto;

import java.util.List;

public record KnockoutBracketDto(
        boolean enabled,
        boolean published,
        List<KnockoutMatchDto> matches
) {}
