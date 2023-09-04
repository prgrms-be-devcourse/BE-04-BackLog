package dev.backlog.domain.auth.model.oauth.dto;

import dev.backlog.domain.auth.model.oauth.OAuthProvider;

public record SignupRequest(
        String blogTitle,
        String introduction,
        String authCode,
        OAuthProvider oAuthProvider
) {

    public static SignupRequest of(
            String blogTitle,
            String introduction,
            String authCode,
            OAuthProvider oAuthProvider
    ) {
        return new SignupRequest(
                blogTitle,
                introduction,
                authCode,
                oAuthProvider
        );
    }

}
