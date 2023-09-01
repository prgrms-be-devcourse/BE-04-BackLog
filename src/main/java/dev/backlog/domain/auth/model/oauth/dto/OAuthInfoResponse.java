package dev.backlog.domain.auth.model.oauth.dto;

import dev.backlog.domain.auth.model.oauth.OAuthProvider;
import dev.backlog.domain.user.model.Email;

public record OAuthInfoResponse(
        String nickname,
        String profileImage,
        Email email,
        OAuthProvider oAuthProvider,
        String oAuthProviderId
) {

    public static OAuthInfoResponse of(
            String nickname,
            String profileImage,
            Email email,
            OAuthProvider oAuthProvider,
            String oAuthProviderId
    ) {
        return new OAuthInfoResponse(
                nickname,
                profileImage,
                email,
                oAuthProvider,
                oAuthProviderId
        );
    }

}
