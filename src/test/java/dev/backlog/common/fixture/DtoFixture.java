package dev.backlog.common.fixture;

import dev.backlog.domain.auth.AuthTokens;
import dev.backlog.domain.auth.model.oauth.OAuthProvider;
import dev.backlog.domain.auth.model.oauth.dto.SignupRequest;

public class DtoFixture {

    public static SignupRequest 회원가입정보() {
        return SignupRequest.of(
                "블로그 제목",
                "소개",
                "authCode",
                OAuthProvider.KAKAO
        );
    }

    public static AuthTokens 토큰생성() {
        return AuthTokens.of(
                "accessToken",
                "refreshToken",
                "Bearer ",
                1000L
        );
    }

}
