package dev.backlog.domain.post.service;

import dev.backlog.config.TestContainerConfig;
import dev.backlog.domain.comment.infrastructure.persistence.CommentRepository;
import dev.backlog.domain.comment.model.Comment;
import dev.backlog.domain.post.dto.PostCreateRequest;
import dev.backlog.domain.post.dto.PostResponse;
import dev.backlog.domain.post.infrastructure.persistence.PostRepository;
import dev.backlog.domain.post.model.Post;
import dev.backlog.domain.user.infrastructure.persistence.UserRepository;
import dev.backlog.domain.user.model.Email;
import dev.backlog.domain.user.model.OAuthProvider;
import dev.backlog.domain.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static dev.backlog.domain.user.model.OAuthProvider.KAKAO;
import static org.assertj.core.api.Assertions.assertThat;

@Import(value = {TestContainerConfig.class})
@ExtendWith(TestContainerConfig.class)
@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("게시글을 상세 조회할 수 있다.")
    @Test
    void findPostById() {
        //given
        User user = User.builder()
                .oauthProvider(OAuthProvider.KAKAO)
                .oauthProviderId(String.valueOf(UUID.randomUUID()))
                .nickname("test")
                .email(new Email("test"))
                .profileImage("test")
                .introduction("test")
                .blogTitle("test")
                .build();
        userRepository.save(user);

        Post post = Post.builder()
                .series(null)
                .user(user)
                .title("test")
                .content("test")
                .summary("test")
                .isPublic(true)
                .thumbnailImage("test")
                .path("test")
                .build();
        postRepository.save(post);

        Comment comment1 = Comment.builder()
                .writer(user)
                .post(post)
                .content("test1")
                .isDeleted(false)
                .build();
        Comment comment2 = Comment.builder()
                .writer(user)
                .post(post)
                .content("test2")
                .isDeleted(false)
                .build();
        commentRepository.saveAll(List.of(comment1, comment2));

        //when
        PostResponse postResponse = postService.findPostById(post.getId(), user.getId());

        //then
        int expectedCommentsSize = 2;
        long increasedViewCount = 1L;

        assertThat(postResponse.postId()).isEqualTo(post.getId());
        assertThat(postResponse.series()).isNull();
        assertThat(postResponse.userId()).isEqualTo(user.getId());
        assertThat(postResponse.title()).isEqualTo(post.getTitle());
        assertThat(postResponse.viewCount()).isEqualTo(increasedViewCount);
        assertThat(postResponse.content()).isEqualTo(post.getContent());
        assertThat(postResponse.summary()).isEqualTo(post.getSummary());
        assertThat(postResponse.isPublic()).isTrue();
        assertThat(postResponse.createdAt()).isEqualTo(post.getCreatedAt());
        assertThat(postResponse.comments()).hasSize(expectedCommentsSize);
        assertThat(postResponse.comments().get(0).commentId()).isEqualTo(comment1.getId());
        assertThat(postResponse.comments().get(0).writer().userId()).isEqualTo(user.getId());
        assertThat(postResponse.comments().get(0).writer().nickname()).isEqualTo(user.getNickname());
        assertThat(postResponse.comments().get(0).content()).isEqualTo(comment1.getContent());
        assertThat(postResponse.comments().get(0).createdAt()).isEqualTo(comment1.getCreatedAt());

        assertThat(postResponse.comments().get(1).commentId()).isEqualTo(comment2.getId());
        assertThat(postResponse.comments().get(1).writer().userId()).isEqualTo(user.getId());
        assertThat(postResponse.comments().get(1).writer().nickname()).isEqualTo(user.getNickname());
        assertThat(postResponse.comments().get(1).content()).isEqualTo(comment2.getContent());
        assertThat(postResponse.comments().get(1).createdAt()).isEqualTo(comment2.getCreatedAt());
    }

    @DisplayName("같은 유저는 동일한 게시글의 조회수를 3시간동안 올릴 수 없다.")
    @Test
    void sameUserCannotIncreaseViewCountForSamePostWithin3Hours() {
        //given
        User user = User.builder()
                .oauthProvider(OAuthProvider.KAKAO)
                .oauthProviderId(String.valueOf(UUID.randomUUID()))
                .nickname("test")
                .email(new Email("test"))
                .profileImage("test")
                .introduction("test")
                .blogTitle("test")
                .build();
        userRepository.save(user);

        Post post = Post.builder()
                .series(null)
                .user(user)
                .title("test")
                .content("test")
                .summary("test")
                .isPublic(true)
                .thumbnailImage("test")
                .path("test")
                .build();
        postRepository.save(post);

        //when
        PostResponse firstSamePostResponse = postService.findPostById(post.getId(), user.getId());
        PostResponse secondSamePostResponse = postService.findPostById(post.getId(), user.getId());

        //then
        long increasedViewCount = 1L;

        assertThat(secondSamePostResponse.viewCount()).isEqualTo(increasedViewCount);
        assertThat(firstSamePostResponse.viewCount()).isEqualTo(secondSamePostResponse.viewCount());
    }

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
        assertThat(postId).isNotNull();
    }

}
