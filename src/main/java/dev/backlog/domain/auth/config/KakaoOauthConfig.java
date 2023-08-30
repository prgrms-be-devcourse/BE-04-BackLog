package dev.backlog.domain.auth.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "oauth.kakao")
public class KakaoOauthConfig {

    public static final String AUTHORIZE_URL = "/oauth/authorize";
    public static final String REQUEST_TOKEN_URL = "/oauth/token";
    public static final String REQUEST_INFO_URL = "/v2/user/me";

    private String authUrl;
    private String apiUrl;
    private String redirectUrl;

    @Value("${oauth.kakao.client-id}")
    private String clientId;

}
