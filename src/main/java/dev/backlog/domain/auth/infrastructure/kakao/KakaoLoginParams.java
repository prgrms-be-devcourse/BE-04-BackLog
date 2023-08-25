package dev.backlog.domain.auth.infrastructure.kakao;

import dev.backlog.domain.auth.model.oauth.OAuthLoginParams;
import dev.backlog.domain.user.model.OAuthProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@NoArgsConstructor
public class KakaoLoginParams implements OAuthLoginParams {

    private String authorizationCode;
    private String blogTitle;

    @Override
    public OAuthProvider oauthProvider() {
        return OAuthProvider.KAKAO;
    }


    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        return body;
    }

}
