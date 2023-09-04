package dev.backlog.domain.auth;

public record AuthTokens(
        String accessToken,
        String refreshToken,
        String grantType,
        Long expiresIn
) {

    public static AuthTokens of(
            String accessToken,
            String refreshToken,
            String grantType,
            Long expiresIn
    ) {
        return new AuthTokens(
                accessToken,
                refreshToken,
                grantType,
                expiresIn
        );
    }

}
