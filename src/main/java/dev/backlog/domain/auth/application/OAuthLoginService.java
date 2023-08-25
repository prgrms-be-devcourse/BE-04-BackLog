package dev.backlog.domain.auth.application;

import dev.backlog.domain.auth.model.AuthTokens;
import dev.backlog.domain.auth.model.AuthTokensGenerator;
import dev.backlog.domain.auth.model.oauth.OAuthInfoResponse;
import dev.backlog.domain.auth.model.oauth.OAuthLoginParams;
import dev.backlog.domain.auth.model.oauth.RequestOAuthInfoService;
import dev.backlog.domain.user.infrastructure.persistence.UserRepository;
import dev.backlog.domain.user.model.Email;
import dev.backlog.domain.user.model.OAuthProvider;
import dev.backlog.domain.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OAuthLoginService {

    private static final String DEFAULT_BLOG_TITLE = ".log";
    private static final String DEFAULT_PROFILE_IMAGE_URL = "기본 프로필 사진 URL";

    private final UserRepository userRepository;
    private final AuthTokensGenerator authTokensGenerator;
    private final RequestOAuthInfoService requestOAuthInfoService;

    public AuthTokens login(OAuthLoginParams params) {
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
        Long userId = findOrCreateUser(oAuthInfoResponse);
        return authTokensGenerator.generate(userId);
    }

    private Long findOrCreateUser(OAuthInfoResponse response) {
        return userRepository.findByEmail(response.getEmail())
                .map(User::getId)
                .orElseGet(() -> newUser(response));
    }

    private Long newUser(OAuthInfoResponse response) {
        checkUser(response.getOauthProviderId(), response.getOauthProvider());
        String profileImage = checkProfileImage(response.getProfileImage());
        String blogTitle = checkBlogTitle(
                response.getBlogTitle(),
                response.getNickname()
        );

        User user = new User(
                response.getNickname(),
                new Email(response.getEmail()),
                profileImage,
                blogTitle,
                response.getOauthProvider()
        );
        return userRepository.save(user).getId();
    }

    private void checkUser(Long oauthProviderId, OAuthProvider oauthProvider) {
        Optional<User> findUser = userRepository.findByOauthProviderIdAndOauthProvider(oauthProviderId.toString(), oauthProvider);
        if (findUser.isPresent() && Objects.equals(findUser.get().getOauthProviderId(), oauthProviderId.toString())) {
            throw new IllegalArgumentException("이미 등록된 사용자입니다.");
        }
    }

    private String checkBlogTitle(String blogTitle, String nickname) {
        if (Objects.isNull(blogTitle) || blogTitle.isEmpty()) {
            return nickname + DEFAULT_BLOG_TITLE;
        }
        return blogTitle;
    }

    private String checkProfileImage(String profileImage) {
        if (Objects.isNull(profileImage) || profileImage.isEmpty()) {
            profileImage = DEFAULT_PROFILE_IMAGE_URL;
        }
        return profileImage;
    }

}
