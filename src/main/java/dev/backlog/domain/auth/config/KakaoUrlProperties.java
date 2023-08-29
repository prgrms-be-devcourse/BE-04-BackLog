package dev.backlog.domain.auth.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "oauth.kakao.url")
public class KakaoUrlProperties {

    private String authUrl;
    private String apiUrl;
    private String redirectUrl;

}
