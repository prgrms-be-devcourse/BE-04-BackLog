package dev.backlog.domain.auth.model;

import dev.backlog.domain.auth.infrastructure.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class AuthTokensGenerator {

    private static final String BEARER_TYPE = "Bearer";
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.access-token-expire-time}")
    private Long accessTokenExpireTime;

    @Value("${jwt.refresh-token-expire-time}")
    private Long refreshTokenExpireTime;

    public AuthTokens generate(Long userId) {
        long now = (new Date()).getTime();
        Date accessTokenExpiredAt = new Date(now + accessTokenExpireTime);
        Date refreshTokenExpiredAt = new Date(now + refreshTokenExpireTime);

        String subject = userId.toString();
        String accessToken = jwtTokenProvider.generate(subject, accessTokenExpiredAt);
        String refreshToken = jwtTokenProvider.generate(subject, refreshTokenExpiredAt);

        return AuthTokens.of(accessToken, refreshToken, BEARER_TYPE, accessTokenExpireTime / 1000L);
    }

}
