package dev.backlog.domain.auth.infrastructure.kakao.authcode;

import dev.backlog.domain.auth.config.KakaoOauthConfig;
import dev.backlog.domain.auth.model.oauth.OAuthProvider;
import dev.backlog.domain.auth.model.oauth.authcode.AuthCodeRequestUrlProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import static dev.backlog.domain.auth.config.KakaoOauthConfig.AUTHORIZE_URL;

@Component
@RequiredArgsConstructor
public class KakaoAuthCodeRequestUrlProvider implements AuthCodeRequestUrlProvider {

    private static final String RESPONSE_TYPE = "code";

    private final KakaoOauthConfig kakaoOauthConfig;

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public String provide() {
        return UriComponentsBuilder
                .fromUriString(kakaoOauthConfig.getUrl().getAuthUrl() + AUTHORIZE_URL)
                .queryParam("response_type", RESPONSE_TYPE)
                .queryParam("client_id", kakaoOauthConfig.getClientId())
                .queryParam("redirect_uri", kakaoOauthConfig.getUrl().getRedirectUrl())
                .toUriString();
    }

}
