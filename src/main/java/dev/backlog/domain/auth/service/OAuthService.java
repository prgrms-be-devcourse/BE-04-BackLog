package dev.backlog.domain.auth.service;

import dev.backlog.domain.auth.model.oauth.authcode.AuthCodeRequestUrlProviderComposite;
import dev.backlog.domain.auth.model.oauth.OAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final AuthCodeRequestUrlProviderComposite authCodeRequestUrlProviderComposite;

    public String getAuthCodeRequestUrl(OAuthProvider oAuthProvider) {
        return authCodeRequestUrlProviderComposite.provide(oAuthProvider);
    }

}
