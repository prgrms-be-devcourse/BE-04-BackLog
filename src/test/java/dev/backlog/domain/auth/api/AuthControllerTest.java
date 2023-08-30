package dev.backlog.domain.auth.api;

import dev.backlog.domain.auth.AuthTokens;
import dev.backlog.domain.auth.model.oauth.OAuthProvider;
import dev.backlog.domain.auth.service.OAuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OAuthService oAuthService;

    @DisplayName("사용자가 로그인 버튼을 누르면 해당 서비스의 접근 권한 url로 리다이렉트한다.")
    @Test
    void redirectAuthCodeRequestUrlTest() throws Exception {
        String expectedRedirectUrl = "https://example.com";

        when(oAuthService.getAuthCodeRequestUrl(eq(OAuthProvider.KAKAO))).thenReturn(expectedRedirectUrl);

        mockMvc.perform(get("/api/auth/v2/{oAuthProvider}", OAuthProvider.KAKAO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(expectedRedirectUrl))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void loginTest() throws Exception {
        String code = "authCode";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        String grantType = "Bearer ";
        Long expiresIn = 1000L;

        AuthTokens expectedTokens = AuthTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .grantType(grantType)
                .expiresIn(expiresIn)
                .build();

        when(oAuthService.login(any(), any())).thenReturn(expectedTokens);

        mockMvc.perform(get("/api/auth/v2/login/{oAuthProvider}", OAuthProvider.KAKAO)
                        .param("code", code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(accessToken))
                .andExpect(jsonPath("$.refreshToken").value(refreshToken))
                .andExpect(jsonPath("$.grantType").value(grantType))
                .andExpect(jsonPath("$.expiresIn").value(expiresIn))
                .andDo(MockMvcResultHandlers.print());
    }

}
