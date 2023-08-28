package dev.backlog.domain.auth.model.oauth;

import static java.util.Locale.ENGLISH;

public enum OAuthProvider {
    KAKAO,
    ;

    public static OAuthProvider fromName(String type) {
        return OAuthProvider.valueOf(type.toUpperCase(ENGLISH));
    }

}
