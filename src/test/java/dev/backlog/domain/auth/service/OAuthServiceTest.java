package dev.backlog.domain.auth.service;

import dev.backlog.common.util.TestFixtureUtil;
import dev.backlog.domain.auth.AuthTokens;
import dev.backlog.domain.auth.AuthTokensGenerator;
import dev.backlog.domain.auth.model.oauth.OAuthProvider;
import dev.backlog.domain.auth.model.oauth.authcode.AuthCodeRequestUrlProviderComposite;
import dev.backlog.domain.auth.model.oauth.client.OAuthMemberClientComposite;
import dev.backlog.domain.auth.model.oauth.dto.OAuthInfoResponse;
import dev.backlog.domain.user.infrastructure.persistence.UserRepository;
import dev.backlog.domain.user.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class OAuthServiceTest {

    @InjectMocks
    private OAuthService oAuthService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthCodeRequestUrlProviderComposite authCodeRequestUrlProviderComposite;

    @Mock
    private OAuthMemberClientComposite oAuthMemberClientComposite;

    @Mock
    private AuthTokensGenerator authTokensGenerator;

    @DisplayName("로그인 타입을 입력하면 로그인 타입에 맞는 리다이렉트 할 url을 생성한다.")
    @Test
    void getAuthCodeReqeustUrlTest() {
        String expectedUrl = "https://example.com";
        when(authCodeRequestUrlProviderComposite.provide(any())).thenReturn(expectedUrl);

        String result = oAuthService.getAuthCodeRequestUrl(OAuthProvider.KAKAO);

        Assertions.assertThat(expectedUrl).isEqualTo(result);
    }

    @DisplayName("로그인 타입과 authCode를 받아 로그인에 성공하면 토큰을 생성해 반환한다.")
    @Test
    void loginTest() {
        String authCode = "authCode";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        String grantType = "Bearer ";
        Long expiresIn = 1000L;

        User user = TestFixtureUtil.createUser();
        OAuthInfoResponse response = OAuthInfoResponse.builder()
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .email(user.getEmail())
                .oAuthProvider(user.getOauthProvider())
                .oAuthProviderId(user.getOauthProviderId())
                .build();


        AuthTokens authToken = AuthTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .grantType(grantType)
                .expiresIn(expiresIn)
                .build();

        when(oAuthMemberClientComposite.fetch(any(), any())).thenReturn(response);
        when(userRepository.findByOauthProviderIdAndOauthProvider(user.getOauthProviderId(), user.getOauthProvider())).thenReturn(Optional.of(user));
        when(authTokensGenerator.generate(user.getId())).thenReturn(authToken);

        AuthTokens authTokens = oAuthService.login(OAuthProvider.KAKAO, authCode);

        assertAll(
                () -> Assertions.assertThat(authTokens.getAccessToken()).isEqualTo(accessToken),
                () -> Assertions.assertThat(authTokens.getRefreshToken()).isEqualTo(refreshToken),
                () -> Assertions.assertThat(authTokens.getGrantType()).isEqualTo(grantType),
                () -> Assertions.assertThat(authTokens.getExpiresIn()).isEqualTo(expiresIn)
        );
    }

}
