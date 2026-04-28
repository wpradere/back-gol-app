package com.goltracker.admin.dto;

import com.goltracker.user.domain.User;

import java.time.LocalDateTime;

public record UserSummaryDto(
        Long id,
        String username,
        String email,
        String role,
        String status,
        LocalDateTime createdAt,
        boolean forcePasswordReset
) {
    public static UserSummaryDto from(User u) {
        return new UserSummaryDto(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getRole().name(),
                u.getStatus().name(),
                u.getCreatedAt(),
                u.isForcePasswordReset()
        );
    }
}
