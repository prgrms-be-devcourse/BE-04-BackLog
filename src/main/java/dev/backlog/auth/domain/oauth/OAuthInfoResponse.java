package dev.backlog.auth.domain.oauth;

import dev.backlog.domain.user.model.OAuthProvider;

public interface OAuthInfoResponse {
    String getEmail();

    String getNickname();

    String getProfileImage();

    String getBlogTitle();

    Long getOauthProviderId();

    OAuthProvider getOauthProvider();
}
