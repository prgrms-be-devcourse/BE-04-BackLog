package dev.backlog.domain.auth.api;

import dev.backlog.domain.auth.AuthTokens;
import dev.backlog.domain.auth.service.OAuthService;
import dev.backlog.domain.auth.model.oauth.OAuthProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/v2")
public class AuthController {

    private final OAuthService oAuthService;

    @GetMapping("/{oAuthProvider}")
    public ResponseEntity<Void> redirectAuthCodeRequestUrl(@PathVariable OAuthProvider oAuthProvider, HttpServletResponse response) throws IOException {
        String redirectUrl = oAuthService.getAuthCodeRequestUrl(oAuthProvider);
        response.sendRedirect(redirectUrl);

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/login/{oAuthProvider}")
    public ResponseEntity<AuthTokens> login(@PathVariable OAuthProvider oAuthProvider, @RequestParam String code) {
        return ResponseEntity.ok(oAuthService.login(oAuthProvider, code));
    }

}
