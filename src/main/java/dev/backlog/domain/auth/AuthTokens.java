package dev.backlog.domain.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class AuthTokens {

    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long expiresIn;

    @Builder
    private AuthTokens(
            String accessToken,
            String refreshToken,
            String grantType,
            Long expiresIn
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.grantType = grantType;
        this.expiresIn = expiresIn;
    }

}
