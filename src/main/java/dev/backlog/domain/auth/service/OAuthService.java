package dev.backlog.domain.auth.service;

import dev.backlog.domain.auth.AuthTokens;
import dev.backlog.domain.auth.AuthTokensGenerator;
import dev.backlog.domain.auth.model.oauth.OAuthProvider;
import dev.backlog.domain.auth.model.oauth.authcode.AuthCodeRequestUrlProviderComposite;
import dev.backlog.domain.auth.model.oauth.client.OAuthMemberClientComposite;
import dev.backlog.domain.auth.model.oauth.dto.OAuthInfoResponse;
import dev.backlog.domain.user.infrastructure.persistence.UserRepository;
import dev.backlog.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final AuthCodeRequestUrlProviderComposite authCodeRequestUrlProviderComposite;
    private final OAuthMemberClientComposite oauthMemberClientComposite;
    private final UserRepository userRepository;
    private final AuthTokensGenerator authTokensGenerator;

    public String getAuthCodeRequestUrl(OAuthProvider oAuthProvider) {
        return authCodeRequestUrlProviderComposite.provide(oAuthProvider);
    }

    public AuthTokens login(OAuthProvider oauthProvider, String authCode) {
        OAuthInfoResponse response = oauthMemberClientComposite.fetch(oauthProvider, authCode);
        User findUser = userRepository.findByOauthProviderIdAndOauthProvider(String.valueOf(response.oAuthProviderId()), response.oAuthProvider())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자는 존재하지 않습니다. 회원가입을 먼저 진행해주세요."));

        return authTokensGenerator.generate(findUser.getId());
    }

}
