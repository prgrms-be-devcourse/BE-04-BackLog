package dev.backlog.domain.auth.api;

import dev.backlog.domain.auth.AuthTokens;
import dev.backlog.domain.auth.service.OAuthService;
import dev.backlog.domain.auth.model.oauth.OAuthProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/v2")
public class AuthController {

    private final OAuthService oAuthService;

    @GetMapping("/{oAuthProvider}")
    @ResponseStatus(FOUND)
    public void redirectAuthCodeRequestUrl(@PathVariable OAuthProvider oAuthProvider, HttpServletResponse response) throws IOException {
        String redirectUrl = oAuthService.getAuthCodeRequestUrl(oAuthProvider);
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/login/{oAuthProvider}")
    @ResponseStatus(OK)
    public AuthTokens login(@PathVariable OAuthProvider oAuthProvider, @RequestParam String code) { // 반환 타입 수정 예정
        return oAuthService.login(oAuthProvider, code);
    }

}
