package dev.backlog.auth.domain.oauth;

import dev.backlog.domain.user.model.OAuthProvider;
import org.springframework.util.MultiValueMap;

public interface OAuthLoginParams {
    OAuthProvider oauthProvider();

    MultiValueMap<String, String> makeBody();
}
