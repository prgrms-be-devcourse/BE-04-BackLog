package dev.backlog.domain.auth.model.oauth.dto;

import dev.backlog.domain.auth.model.oauth.OAuthProvider;
import lombok.Builder;

@Builder
public record SignupRequest(
        String blogTitle,
        String introduction,
        String authCode,
        OAuthProvider oAuthProvider
) {
}
