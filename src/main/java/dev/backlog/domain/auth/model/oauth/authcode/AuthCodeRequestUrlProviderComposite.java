package dev.backlog.domain.auth.model.oauth.authcode;

import dev.backlog.domain.auth.model.oauth.OAuthProvider;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AuthCodeRequestUrlProviderComposite {

    private final Map<OAuthProvider, AuthCodeRequestUrlProvider> mapping;

    public AuthCodeRequestUrlProviderComposite(Set<AuthCodeRequestUrlProvider> providers) {
        this.mapping = providers.stream().collect(
                Collectors.toUnmodifiableMap(
                        AuthCodeRequestUrlProvider::oAuthProvider,
                        Function.identity()
                )
        );
    }

    public String provide(OAuthProvider oAuthProvider) {
        return getProvider(oAuthProvider).provide();
    }

    public AuthCodeRequestUrlProvider getProvider(OAuthProvider oAuthProvider) {
        return Optional.ofNullable(mapping.get(oAuthProvider))
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 로그인 타입입니다."));
    }

}
