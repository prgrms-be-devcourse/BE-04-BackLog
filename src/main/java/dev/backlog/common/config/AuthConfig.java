package dev.backlog.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AuthConfig {

    public static final String RESPONSE_TYPE = "code";
    public static final String BEARER_TYPE = "Bearer";
    public static final String GRANT_TYPE = "authorization_code";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
