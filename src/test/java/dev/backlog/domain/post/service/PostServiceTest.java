package dev.backlog.domain.post.service;

import dev.backlog.domain.post.dto.PostCreateRequest;
import dev.backlog.domain.user.infrastructure.persistence.UserRepository;
import dev.backlog.domain.user.model.Email;
import dev.backlog.domain.user.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static dev.backlog.domain.user.model.OAuthProvider.KAKAO;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostService postService;

    @DisplayName("포스트 생성요청과 유저의 아이디를 받아 게시물을 저장할 수 있다.")
    @Test
    void createTest() {
        User user = User.builder()
                .oauthProvider(KAKAO)
                .oauthProviderId("test")
                .nickname("test")
                .email(new Email("test@example.com"))
                .profileImage("image")
                .blogTitle("blogTitle")
                .build();
        User savedUser = userRepository.save(user);

        PostCreateRequest request = new PostCreateRequest(
                null,
                "제목",
                "내용",
                null,
                "요약",
                true,
                "url",
                "/path"
        );

        Long postId = postService.create(request, savedUser.getId());
        Assertions.assertThat(postId).isOne();
    }

}
