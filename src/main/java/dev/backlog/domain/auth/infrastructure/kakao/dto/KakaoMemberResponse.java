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
        return OAuthInfoResponse.builder()
                .oAuthProvider(OAuthProvider.KAKAO)
                .oAuthProviderId(String.valueOf(id))
                .nickname(kakaoAccount.profile.nickname)
                .email(new Email(kakaoAccount.email))
                .profileImage(kakaoAccount.profile.profileImageUrl)
                .build();
    }

    @JsonNaming(SnakeCaseStrategy.class)
    public record KakaoAccount(
            Profile profile,
            String email
    ) {
    }

    @JsonNaming(SnakeCaseStrategy.class)
    public record Profile(
            String nickname,
            String profileImageUrl
    ) {
    }

}
