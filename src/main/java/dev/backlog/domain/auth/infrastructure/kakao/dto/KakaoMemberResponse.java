package dev.backlog.domain.auth.infrastructure.kakao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import dev.backlog.domain.auth.model.oauth.OAuthProvider;
import dev.backlog.domain.auth.model.oauth.dto.OAuthInfoResponse;
import dev.backlog.domain.user.model.Email;

@JsonNaming(SnakeCaseStrategy.class)
public record KakaoMemberResponse(
        Long id,
        KakaoAccount kakaoAccount
) {
    public OAuthInfoResponse toOAuthInfoResponse() {
        return OAuthInfoResponse.of(
                kakaoAccount.profile.nickname,
                kakaoAccount.profile.profileImageUrl,
                new Email(kakaoAccount.email),
                OAuthProvider.KAKAO,
                String.valueOf(id)
        );
    }

    @JsonNaming(SnakeCaseStrategy.class)
    private record KakaoAccount(
            Profile profile,
            String email
    ) {
    }

    @JsonNaming(SnakeCaseStrategy.class)
    private record Profile(
            String nickname,
            String profileImageUrl
    ) {
    }

}
