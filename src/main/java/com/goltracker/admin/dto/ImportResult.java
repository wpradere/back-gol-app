package com.goltracker.admin.dto;

import java.util.List;

public record ImportResult(
        int imported,
        int updated,
        int errors,
        List<String> errorDetails
) {}
