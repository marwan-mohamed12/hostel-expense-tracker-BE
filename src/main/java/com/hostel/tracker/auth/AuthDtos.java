package com.hostel.tracker.auth;

import jakarta.validation.constraints.NotBlank;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {
    }

    public record UserResponse(
            String id,
            String username,
            String displayName,
            String role
    ) {
        public static UserResponse from(AppUser user) {
            return new UserResponse(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole().name());
        }
    }

    public record LoginResponse(
            String accessToken,
            String expiresAt,
            UserResponse user
    ) {
    }
}
