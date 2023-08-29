package dev.backlog.domain.auth.infrastructure.kakao.client;

import dev.backlog.domain.auth.infrastructure.kakao.KakaoTokens;
import dev.backlog.domain.auth.infrastructure.kakao.dto.KakaoMemberResponse;
import dev.backlog.domain.auth.model.oauth.OAuthProvider;
import dev.backlog.domain.auth.model.oauth.client.OAuthMemberClient;
import dev.backlog.domain.auth.model.oauth.dto.OAuthInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoMemberClient implements OAuthMemberClient {

    private static final String BEARER_TYPE = "Bearer";

    private final KakaoApiClient kakaoApiClient;

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public OAuthInfoResponse fetch(String authCode){
        KakaoTokens tokenInfo = kakaoApiClient.fetchToken(authCode);
        KakaoMemberResponse response = kakaoApiClient.fetchMember(BEARER_TYPE + tokenInfo.accessToken());

        return response.toOAuthInfoResponse();
    }

}
