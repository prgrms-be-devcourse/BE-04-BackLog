package dev.backlog.domain.auth.infrastructure.kakao.authcode;

import dev.backlog.domain.auth.infrastructure.kakao.config.KakaoOauthConfig;
import dev.backlog.domain.auth.model.oauth.authcode.AuthCodeRequestUrlProvider;
import dev.backlog.domain.auth.model.oauth.OAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import static dev.backlog.domain.auth.config.AuthConfig.RESPONSE_TYPE;

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
                .fromUriString(kakaoOauthConfig.getAuthorizeUrl())
                .queryParam("response_type", RESPONSE_TYPE)
                .queryParam("client_id", kakaoOauthConfig.getClientId())
                .queryParam("redirect_uri", kakaoOauthConfig.getRedirectUrl())
                .toUriString();
    }

}
