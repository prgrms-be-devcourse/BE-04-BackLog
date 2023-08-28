package dev.backlog.domain.auth.infrastructure.kakao.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "oauth.kakao.url")
public class KakaoUrlProperties {
    public String authUrl;
    public String apiUrl;
    public String redirectUrl;

}
