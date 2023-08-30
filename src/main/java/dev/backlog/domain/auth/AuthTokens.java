package dev.backlog.domain.auth;

import lombok.Builder;

@Builder
public record AuthTokens(
        String accessToken,
        String refreshToken,
        String grantType,
        Long expiresIn
) {
}
