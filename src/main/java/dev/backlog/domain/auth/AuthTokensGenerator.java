package dev.backlog.domain.auth;

import dev.backlog.domain.auth.model.oauth.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AuthTokensGenerator {

    private static final String BEARER_TYPE = "Bearer";

    private final JwtTokenProvider jwtTokenProvider;
    private final Long accessTokenExpireTime;
    private final Long refreshTokenExpireTime;

    public AuthTokensGenerator(JwtTokenProvider jwtTokenProvider,
                               @Value("${jwt.access-token-expire-time}") Long accessTokenExpireTime,
                               @Value("${jwt.refresh-token-expire-time}") Long refreshTokenExpireTime
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }

    public AuthTokens generate(Long userId) {
        long now = (new Date()).getTime();
        Date accessTokenExpiredAt = new Date(now + accessTokenExpireTime);
        Date refreshTokenExpiredAt = new Date(now + refreshTokenExpireTime);

        String subject = userId.toString();
        String accessToken = jwtTokenProvider.generate(subject, accessTokenExpiredAt);
        String refreshToken = jwtTokenProvider.generate(subject, refreshTokenExpiredAt);

        return AuthTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .grantType(BEARER_TYPE)
                .expiresIn(accessTokenExpireTime / 1000L)
                .build();
    }

}
