package dev.backlog.domain.auth.infrastructure.kakao.authcode;

import dev.backlog.common.config.KakaoOauthConfig;
import dev.backlog.domain.auth.model.oauth.authcode.AuthCodeRequestUrlProvider;
import dev.backlog.domain.auth.model.oauth.OAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import static dev.backlog.common.config.AuthConfig.RESPONSE_TYPE;
import static dev.backlog.common.config.KakaoOauthConfig.AUTHORIZE_URL;

@Component
@RequiredArgsConstructor
public class KakaoAuthCodeRequestUrlProvider implements AuthCodeRequestUrlProvider {

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
                .queryParam("redirect_uri", kakaoOauthConfig.getUrl().redirectUrl)
                .toUriString();
    }

}
