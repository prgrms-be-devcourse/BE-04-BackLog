package dev.backlog.domain.auth.model.oauth.client;

import dev.backlog.domain.auth.model.oauth.OAuthProvider;
import dev.backlog.domain.auth.model.oauth.dto.OAuthInfoResponse;

public interface OAuthMemberClient {

    OAuthProvider oAuthProvider();

    OAuthInfoResponse fetch(String authCode);

}
