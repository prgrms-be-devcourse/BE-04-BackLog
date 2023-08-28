package dev.backlog.domain.auth.model.oauth.authcode;

import dev.backlog.domain.auth.model.oauth.OAuthProvider;

public interface AuthCodeRequestUrlProvider {

    OAuthProvider oAuthProvider();

    String provide();

}
