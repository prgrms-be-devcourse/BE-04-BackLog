package dev.backlog.domain.auth.model.oauth;

import dev.backlog.domain.user.model.OAuthProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RequestOAuthInfoService {

    private final Map<OAuthProvider, OAuthApiClient> users;

    public RequestOAuthInfoService(List<OAuthApiClient> users) {
        this.users = users.stream().collect(
                Collectors.toUnmodifiableMap(OAuthApiClient::oAuthProvider, Function.identity())
        );
    }

    public OAuthInfoResponse request(OAuthLoginParams params) {
        OAuthApiClient user = users.get(params.oauthProvider());
        String accessToken = user.requestAccessToken(params);
        return user.requestOauthInfo(accessToken);
    }
}
