package dev.backlog.domain.auth.infrastructure.kakao;


import dev.backlog.domain.auth.model.oauth.OAuthApiClient;
import dev.backlog.domain.auth.model.oauth.OAuthInfoResponse;
import dev.backlog.domain.auth.model.oauth.OAuthLoginParams;
import dev.backlog.domain.user.model.OAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoApiClient implements OAuthApiClient {

    private static final String GRANT_TYPE = "authorization_code";
    private final RestTemplate restTemplate;
    @Value("${oauth.kakao.url.auth}")
    private String authUrl;
    @Value("${oauth.kakao.url.api}")
    private String apiUrl;
    @Value("${oauth.kakao.client-id}")
    private String clientId;

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public String requestAccessToken(OAuthLoginParams params) {
        String url = authUrl + "/oauth/token";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = params.makeBody();
        body.add("grant_type", GRANT_TYPE);
        body.add("client_id", clientId);

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        KakaoTokens response = restTemplate.postForObject(url, request, KakaoTokens.class);

        return response.getAccessToken();
    }

    @Override
    public OAuthInfoResponse requestOauthInfo(String accessToken) {
        String url = apiUrl + "/v2/user/me";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "Bearer " + accessToken);

        HttpEntity<?> request = new HttpEntity<>(httpHeaders);

        return restTemplate.exchange(url, HttpMethod.GET, request, KakaoInfoResponse.class).getBody();
    }

}
