package dev.backlog.domain.auth.infrastructure.kakao.client;

import dev.backlog.domain.auth.infrastructure.kakao.KakaoTokens;
import dev.backlog.domain.auth.infrastructure.kakao.dto.KakaoMemberResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoApiClient {

    private static final String REQUEST_TOKEN_URL = "https://kauth.kakao.com//oauth/token";
    private static final String REQUEST_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String BEARER_TYPE = "Bearer";

    private final RestTemplate restTemplate;
    private final String clientId;
    private final String redirectUrl;
    private final String clientSecret;

    public KakaoApiClient(RestTemplate restTemplate,
                          @Value("${oauth.kakao.client-id}") String clientId,
                          @Value("${oauth.kakao.redirect-url}") String redirectUrl,
                          @Value("${oauth.kakao.client-secret}") String clientSecret
    ) {
        this.restTemplate = restTemplate;
        this.clientId = clientId;
        this.redirectUrl = redirectUrl;
        this.clientSecret = clientSecret;
    }

    public KakaoTokens fetchToken(String authCode) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", GRANT_TYPE);
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUrl);
        body.add("client_secret", clientSecret);
        body.add("code", authCode);

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        KakaoTokens response = restTemplate.postForObject(REQUEST_TOKEN_URL, request, KakaoTokens.class);

        assert response != null;
        return response;
    }

    public KakaoMemberResponse fetchMember(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", BEARER_TYPE + accessToken);

        HttpEntity<?> request = new HttpEntity<>(httpHeaders);

        KakaoMemberResponse response = restTemplate.exchange(REQUEST_INFO_URL, HttpMethod.GET, request, KakaoMemberResponse.class).getBody();

        assert response != null;
        return response;
    }

}
