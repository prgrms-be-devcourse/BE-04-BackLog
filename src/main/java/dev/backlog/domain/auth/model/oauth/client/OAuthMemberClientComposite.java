package dev.backlog.domain.auth.model.oauth.client;

import dev.backlog.domain.auth.model.oauth.OAuthProvider;
import dev.backlog.domain.auth.model.oauth.dto.OAuthInfoResponse;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OAuthMemberClientComposite {

    private final Map<OAuthProvider, OAuthMemberClient> mapping;

    public OAuthMemberClientComposite(Set<OAuthMemberClient> clients) {
        this.mapping = clients.stream().collect(
                Collectors.toUnmodifiableMap(OAuthMemberClient::oAuthProvider, Function.identity())
        );
    }

    public OAuthInfoResponse fetch(OAuthProvider oAuthProvider, String authCode){
        return getClient(oAuthProvider).fetch(authCode);
    }

    public OAuthMemberClient getClient(OAuthProvider oAuthProvider){
        return Optional.ofNullable(mapping.get(oAuthProvider))
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 로그인 타입입니다."));
    }

}
