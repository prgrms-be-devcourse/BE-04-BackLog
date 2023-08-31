package dev.backlog.domain.auth.api;

import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.backlog.domain.auth.AuthTokens;
import dev.backlog.domain.auth.model.oauth.OAuthProvider;
import dev.backlog.domain.auth.model.oauth.dto.SignupRequest;
import dev.backlog.domain.auth.service.OAuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ExtendWith({RestDocumentationExtension.class})
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OAuthService oAuthService;

    @DisplayName("사용자가 로그인 버튼을 누르면 해당하는 서비스의 접근 권한 url로 리다이렉트한다.")
    @Test
    void redirectAuthCodeRequestUrlTest() throws Exception {
        String expectedRedirectUrl = "https://example.com";

        when(oAuthService.getAuthCodeRequestUrl(eq(OAuthProvider.KAKAO))).thenReturn(expectedRedirectUrl);

        mockMvc.perform(get("/api/auth/v2/{oAuthProvider}", OAuthProvider.KAKAO)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(document("auth-redirect",
                                resourceDetails().tag("Auth").description("접근 권한 url 리다이렉트"),
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("oAuthProvider").description("OAuth 로그인 타입")
                                ),
                                responseHeaders(
                                        headerWithName("Location").description("리다이렉션 대상 URL")
                                )
                        )
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(expectedRedirectUrl));
    }

    @DisplayName("올바른 로그인 타입과 인증 코드, 추가 정보를 받아 회원가입에 성공한다.")
    @Test
    void signupTest() throws Exception {
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

        AuthTokens expectedTokens = AuthTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .grantType(grantType)
                .expiresIn(expiresIn)
                .build();

        when(oAuthService.signup(signupRequest)).thenReturn(expectedTokens);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/auth/v2/signup")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest))
                )
                .andDo(document("auth-signup",
                                resourceDetails().tags("Auth").description("회원가입")
                                        .requestSchema(Schema.schema("SignupRequest"))
                                        .responseSchema(Schema.schema("AuthTokens")),
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("blogTitle").type(JsonFieldType.STRING).description("블로그 제목"),
                                        fieldWithPath("introduction").type(JsonFieldType.STRING).description("소개"),
                                        fieldWithPath("oAuthProvider").type(JsonFieldType.STRING).description("로그인 타입"),
                                        fieldWithPath("authCode").type(JsonFieldType.STRING).description("인증 코드")
                                ),
                                responseFields(
                                        fieldWithPath("accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
                                        fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"),
                                        fieldWithPath("grantType").type(JsonFieldType.STRING).description("Bearer 타입"),
                                        fieldWithPath("expiresIn").type(JsonFieldType.NUMBER).description("토큰 만료 시간(초)")
                                )
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(accessToken))
                .andExpect(jsonPath("$.refreshToken").value(refreshToken))
                .andExpect(jsonPath("$.grantType").value(grantType))
                .andExpect(jsonPath("$.expiresIn").value(expiresIn)
                );
    }

    @DisplayName("올바른 로그인 타입과 인증 코드를 받아 로그인에 성공한다.")
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
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("code", code)
                )
                .andDo(document("auth-login",
                                resourceDetails().tag("Auth").description("로그인")
                                        .responseSchema(Schema.schema("AuthTokens")),
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("oAuthProvider").description("OAuth 로그인 타입")
                                ),
                                queryParameters(
                                        parameterWithName("code").description("인증 코드")
                                ),
                                responseFields(
                                        fieldWithPath("accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
                                        fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"),
                                        fieldWithPath("grantType").type(JsonFieldType.STRING).description("Bearer 타입"),
                                        fieldWithPath("expiresIn").type(JsonFieldType.NUMBER).description("토큰 만료 시간(초)")
                                )
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(accessToken))
                .andExpect(jsonPath("$.refreshToken").value(refreshToken))
                .andExpect(jsonPath("$.grantType").value(grantType))
                .andExpect(jsonPath("$.expiresIn").value(expiresIn)
                );
    }

}
