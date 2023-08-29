package dev.backlog.domain.auth.model.oauth.dto;

import dev.backlog.domain.auth.model.oauth.OAuthProvider;
import dev.backlog.domain.user.model.Email;
import lombok.Builder;

@Builder
public record OAuthInfoResponse(
        String nickname,
        String profileImage,
        Email email,
        OAuthProvider oAuthProvider,
        Long oAuthProviderId
) {
}
