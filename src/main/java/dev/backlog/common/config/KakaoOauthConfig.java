package dev.backlog.common.config;

import dev.backlog.domain.auth.infrastructure.kakao.config.KakaoUrlProperties;
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

    private KakaoUrlProperties url;

    @Value("${oauth.kakao.client-id}")
    private String clientId;

}
