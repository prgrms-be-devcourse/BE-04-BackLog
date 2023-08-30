package dev.backlog.domain.auth.infrastructure.kakao.client;

import dev.backlog.domain.auth.config.KakaoOauthConfig;
import dev.backlog.domain.auth.infrastructure.kakao.KakaoTokens;
import dev.backlog.domain.auth.infrastructure.kakao.dto.KakaoMemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static dev.backlog.domain.auth.config.KakaoOauthConfig.REQUEST_INFO_URL;
import static dev.backlog.domain.auth.config.KakaoOauthConfig.REQUEST_TOKEN_URL;

@Component
@RequiredArgsConstructor
public class KakaoApiClient {

    public static final String GRANT_TYPE = "authorization_code";
    public static final String BEARER_TYPE = "Bearer";

    private final RestTemplate restTemplate;
    private final KakaoOauthConfig kakaoOauthConfig;

    public KakaoTokens fetchToken(String authCode) {
        String url = kakaoOauthConfig.getAuthUrl() + REQUEST_TOKEN_URL;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", GRANT_TYPE);
        body.add("client_id", kakaoOauthConfig.getClientId());
        body.add("redirect_uri", kakaoOauthConfig.getRedirectUrl());
        body.add("code", authCode);

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        KakaoTokens response = restTemplate.postForObject(url, request, KakaoTokens.class);

        assert response != null;
        return response;
    }

    public KakaoMemberResponse fetchMember(String accessToken) {
        String url = kakaoOauthConfig.getApiUrl() + REQUEST_INFO_URL;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", BEARER_TYPE + accessToken);

        HttpEntity<?> request = new HttpEntity<>(httpHeaders);

        KakaoMemberResponse response = restTemplate.exchange(url, HttpMethod.GET, request, KakaoMemberResponse.class).getBody();

        assert response != null;
        return response;
    }

}
