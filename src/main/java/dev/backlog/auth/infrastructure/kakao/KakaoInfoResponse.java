package dev.backlog.auth.infrastructure.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.backlog.auth.domain.oauth.OAuthInfoResponse;
import dev.backlog.domain.user.model.OAuthProvider;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoInfoResponse implements OAuthInfoResponse {

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @JsonProperty("id")
    private Long oauthProviderId;

    private String blogTitle;

    @Override
    public String getEmail() {
        return kakaoAccount.email;
    }

    @Override
    public String getNickname() {
        return kakaoAccount.profile.nickname;
    }

    @Override
    public String getProfileImage() {
        return kakaoAccount.profile.profileImage;
    }

    @Override
    public String getBlogTitle() {
        return blogTitle;
    }

    @Override
    public Long getOauthProviderId() {
        return oauthProviderId;
    }

    @Override
    public OAuthProvider getOauthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class KakaoAccount {
        private Profile profile;
        private String email;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Profile {
        private String nickname;
        private String profileImage;
    }
}
