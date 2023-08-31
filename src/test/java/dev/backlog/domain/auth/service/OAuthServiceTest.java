package dev.backlog.domain.auth.service;

import dev.backlog.common.fixture.TestFixture;
import dev.backlog.domain.auth.AuthTokens;
import dev.backlog.domain.auth.AuthTokensGenerator;
import dev.backlog.domain.auth.model.oauth.OAuthProvider;
import dev.backlog.domain.auth.model.oauth.authcode.AuthCodeRequestUrlProviderComposite;
import dev.backlog.domain.auth.model.oauth.client.OAuthMemberClientComposite;
import dev.backlog.domain.auth.model.oauth.dto.OAuthInfoResponse;
import dev.backlog.domain.auth.model.oauth.dto.SignupRequest;
import dev.backlog.domain.user.infrastructure.persistence.UserRepository;
import dev.backlog.domain.user.model.Email;
import dev.backlog.domain.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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

    @DisplayName("로그인 타입에 맞는 리다이렉트 할 url을 생성한다.")
    @Test
    void getAuthCodeReqeustUrlTest() {
        String expectedUrl = "https://example.com";
        when(authCodeRequestUrlProviderComposite.provide(OAuthProvider.KAKAO)).thenReturn(expectedUrl);

        String result = oAuthService.getAuthCodeRequestUrl(OAuthProvider.KAKAO);
        assertThat(expectedUrl).isEqualTo(result);
    }

    @DisplayName("회원가입에 성공하면 토큰을 생성해 반환한다.")
    @Test
    void signupTest() {
        String authCode = "authCode";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        String grantType = "Bearer ";
        Long expiresIn = 1000L;

        SignupRequest signupRequest = SignupRequest.builder()
                .blogTitle("블로그 제목")
                .introduction("소개")
                .authCode(authCode)
                .oAuthProvider(OAuthProvider.KAKAO)
                .build();

        OAuthInfoResponse response = OAuthInfoResponse.builder()
                .nickname("닉네임")
                .profileImage("프로필 사진")
                .email(new Email("test123@gmail.com"))
                .oAuthProvider(OAuthProvider.KAKAO)
                .oAuthProviderId("1a2b3c")
                .build();

        User newUser = TestFixture.유저1();

        AuthTokens authToken = AuthTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .grantType(grantType)
                .expiresIn(expiresIn)
                .build();

        when(oAuthMemberClientComposite.fetch(signupRequest.oAuthProvider(), signupRequest.authCode())).thenReturn(response);
        when(userRepository.save(any())).thenReturn(newUser);
        when(authTokensGenerator.generate(newUser.getId())).thenReturn(authToken);

        AuthTokens authTokens = oAuthService.signup(signupRequest);

        assertAll(
                () -> assertThat(authTokens.accessToken()).isEqualTo(accessToken),
                () -> assertThat(authTokens.refreshToken()).isEqualTo(refreshToken),
                () -> assertThat(authTokens.grantType()).isEqualTo(grantType),
                () -> assertThat(authTokens.expiresIn()).isEqualTo(expiresIn)
        );
    }

    @DisplayName("로그인에 성공하면 토큰을 생성해 반환한다.")
    @Test
    void loginTest() {
        String authCode = "authCode";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        String grantType = "Bearer ";
        Long expiresIn = 1000L;

        User user = TestFixture.유저1();
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
                () -> assertThat(authTokens.accessToken()).isEqualTo(accessToken),
                () -> assertThat(authTokens.refreshToken()).isEqualTo(refreshToken),
                () -> assertThat(authTokens.grantType()).isEqualTo(grantType),
                () -> assertThat(authTokens.expiresIn()).isEqualTo(expiresIn)
        );
    }

}
